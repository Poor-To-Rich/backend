package com.poortorich.chat.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.request.enums.SortBy;
import com.poortorich.chat.response.AllChatroomsResponse;
import com.poortorich.chat.response.ChatroomCoverInfoResponse;
import com.poortorich.chat.response.ChatroomCreateResponse;
import com.poortorich.chat.response.ChatroomDetailsResponse;
import com.poortorich.chat.response.ChatroomInfoResponse;
import com.poortorich.chat.response.ChatroomLikeStatusResponse;
import com.poortorich.chat.response.ChatroomsResponse;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.like.service.LikeService;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.tag.service.TagService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatFacadeTest {

    @Mock
    private UserService userService;
    @Mock
    private ChatroomService chatroomService;
    @Mock
    private ChatParticipantService chatParticipantService;
    @Mock
    private FileUploadService fileUploadService;
    @Mock
    private TagService tagService;
    @Mock
    private ChatMessageService chatMessageService;
    @Mock
    private LikeService likeService;

    @InjectMocks
    private ChatFacade chatFacade;

    private final Long chatroomId = 1L;
    private final MultipartFile image = Mockito.mock(MultipartFile.class);
    private final String imageUrl = "https://image.com";
    private final String chatroomTitle = "채팅방";
    private final Long maxMemberCount = 10L;
    private final List<String> hashtags = List.of("부자", "거지");
    private final Boolean isRankingEnabled = false;
    private final String chatroomPassword = "부자12";

    private Chatroom chatroom;

    @BeforeEach
    void setUp() {
        chatroom = Chatroom.builder()
                .id(chatroomId)
                .title(chatroomTitle)
                .image(imageUrl)
                .maxMemberCount(maxMemberCount)
                .isRankingEnabled(isRankingEnabled)
                .password(chatroomPassword)
                .isDeleted(false)
                .createdDate(LocalDateTime.of(2025, 8, 3, 15, 24, 51))
                .build();
    }

    @Test
    @DisplayName("채팅방 추가 성공")
    void createChatroomSuccess() {
        String username = "test";
        ChatroomCreateRequest request = new ChatroomCreateRequest(
                image, chatroomTitle, maxMemberCount, null, hashtags, isRankingEnabled, chatroomPassword
        );
        User user = User.builder().username(username).build();

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(fileUploadService.uploadImage(image)).thenReturn(imageUrl);
        when(chatroomService.createChatroom(imageUrl, request)).thenReturn(chatroom);

        ChatroomCreateResponse response = chatFacade.createChatroom(username, request);

        verify(chatParticipantService).createChatroomHost(user, chatroom);
        verify(tagService).createTag(hashtags, chatroom);

        assertThat(response.getNewChatroomId()).isEqualTo(chatroom.getId());
    }

    @Test
    @DisplayName("채팅방 정보 조회 성공")
    void getChatroomSuccess() {
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(tagService.getTagNames(chatroom)).thenReturn(hashtags);

        ChatroomInfoResponse response = chatFacade.getChatroom(chatroomId);

        assertThat(response.getChatroomImage()).isEqualTo(chatroom.getImage());
        assertThat(response.getChatroomTitle()).isEqualTo(chatroom.getTitle());
        assertThat(response.getMaxMemberCount()).isEqualTo(chatroom.getMaxMemberCount());
        assertThat(response.getIsRankingEnabled()).isEqualTo(chatroom.getIsRankingEnabled());
        assertThat(response.getChatroomPassword()).isEqualTo(chatroom.getPassword());

        assertThat(response.getHashtags().get(0)).isEqualTo(hashtags.get(0));
        assertThat(response.getHashtags().get(1)).isEqualTo(hashtags.get(1));
    }

    @Test
    @DisplayName("내가 방장인 채팅방 조회 성공")
    void getHostedChatroomSuccess() {
        User user = User.builder().username("test").build();
        Chatroom chatroom2 = Chatroom.builder().id(2L).build();
        List<Chatroom> hostedChatrooms = List.of(chatroom, chatroom2);

        when(userService.findUserByUsername("test")).thenReturn(user);
        when(chatroomService.getHostedChatrooms(user)).thenReturn(hostedChatrooms);
        when(tagService.getTagNames(chatroom)).thenReturn(hashtags);
        when(tagService.getTagNames(chatroom2)).thenReturn(hashtags);
        when(chatParticipantService.countByChatroom(chatroom)).thenReturn(1L);
        when(chatParticipantService.countByChatroom(chatroom2)).thenReturn(2L);
        when(chatMessageService.getLastMessageTime(chatroom)).thenReturn("2025-07-31T02:30");
        when(chatMessageService.getLastMessageTime(chatroom2)).thenReturn("2025-07-31T03:00");

        ChatroomsResponse response = chatFacade.getHostedChatrooms("test");

        assertThat(response.getChatrooms()).hasSize(2);
        assertThat(response.getChatrooms().get(0).getChatroomId()).isEqualTo(chatroom.getId());
        assertThat(response.getChatrooms().get(1).getChatroomId()).isEqualTo(chatroom2.getId());
    }

    @Test
    @DisplayName("전체 채팅방 목록 조회 성공")
    void getAllChatroomsSuccess() {
        SortBy sortBy = SortBy.UPDATED_AT;
        Long cursor = -1L;

        Chatroom chatroom1 = Chatroom.builder().id(1L).build();
        Chatroom chatroom2 = Chatroom.builder().id(2L).build();

        when(chatroomService.getAllChatrooms(sortBy, cursor)).thenReturn(List.of(chatroom1, chatroom2));
        when(chatroomService.hasNext(sortBy, 2L)).thenReturn(true);
        when(chatroomService.getNextCursor(sortBy, 2L)).thenReturn(3L);
        when(tagService.getTagNames(any())).thenReturn(hashtags);
        when(chatParticipantService.countByChatroom(any())).thenReturn(3L);
        when(chatMessageService.getLastMessageTime(any())).thenReturn("2025-07-31T02:30");

        AllChatroomsResponse response = chatFacade.getAllChatrooms(sortBy, cursor);

        assertThat(response.getChatrooms()).hasSize(2);
        assertThat(response.getHasNext()).isTrue();
        assertThat(response.getNextCursor()).isEqualTo(3L);
        assertThat(response.getChatrooms().get(0).getChatroomId()).isEqualTo(chatroom1.getId());
        assertThat(response.getChatrooms().get(1).getChatroomId()).isEqualTo(chatroom2.getId());
    }

    @Test
    @DisplayName("채팅방 검색 목록 조회 성공")
    void searchChatroomsSuccess() {
        String keyword = "부자";
        Chatroom chatroom1 = Chatroom.builder().id(1L).title("부자되자").build();
        Chatroom chatroom2 = Chatroom.builder().id(2L).title("부자될거야").build();
        List<Chatroom> chatrooms = List.of(chatroom1, chatroom2);

        when(chatroomService.searchChatrooms(keyword)).thenReturn(chatrooms);
        when(tagService.getTagNames(any())).thenReturn(hashtags);
        when(chatParticipantService.countByChatroom(any())).thenReturn(3L);
        when(chatMessageService.getLastMessageTime(any())).thenReturn("2025-07-31T02:30");

        ChatroomsResponse response = chatFacade.searchChatrooms(keyword);

        assertThat(response.getChatrooms()).hasSize(2);
        assertThat(response.getChatrooms().get(0).getChatroomTitle()).isEqualTo(chatroom1.getTitle());
        assertThat(response.getChatrooms().get(1).getChatroomTitle()).isEqualTo(chatroom2.getTitle());
    }

    @Test
    @DisplayName("채팅방 상세 정보 조회 성공")
    void getChatroomDetailsSuccess() {
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatParticipantService.countByChatroom(chatroom)).thenReturn(5L);

        ChatroomDetailsResponse response = chatFacade.getChatroomDetails(chatroomId);

        assertThat(response.getChatroomTitle()).isEqualTo(chatroom.getTitle());
        assertThat(response.getChatroomImage()).isEqualTo(chatroom.getImage());
        assertThat(response.getCurrentMemberCount()).isEqualTo(5L);
        assertThat(response.getIsRankingEnabled()).isFalse();
        assertThat(response.getIsClosed()).isFalse();
    }

    @Test
    @DisplayName("채팅방 커버 정보 조회 성공")
    void getChatroomCoverInfoSuccess() {
        String username = "testUser";
        User user = User.builder()
                .id(1L)
                .username(username)
                .nickname(username)
                .build();
        ChatParticipant hostParticipant = ChatParticipant.builder()
                .user(user)
                .role(ChatroomRole.HOST)
                .rankingStatus(RankingStatus.NONE)
                .build();

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(tagService.getTagNames(chatroom)).thenReturn(hashtags);
        when(chatParticipantService.countByChatroom(chatroom)).thenReturn(3L);
        when(chatParticipantService.isJoined(user, chatroom)).thenReturn(true);
        when(chatParticipantService.getChatroomHost(chatroom)).thenReturn(hostParticipant);

        ChatroomCoverInfoResponse response = chatFacade.getChatroomCoverInfo(username, chatroomId);

        assertThat(response.getChatroomId()).isEqualTo(chatroomId);
        assertThat(response.getChatroomTitle()).isEqualTo(chatroomTitle);
        assertThat(response.getChatroomImage()).isEqualTo(imageUrl);
        assertThat(response.getDescription()).isEqualTo(chatroom.getDescription());
        assertThat(response.getHashtags()).isEqualTo(hashtags);
        assertThat(response.getCurrentMemberCount()).isEqualTo(3L);
        assertThat(response.getMaxMemberCount()).isEqualTo(maxMemberCount);
        assertThat(response.getIsJoined()).isTrue();
        assertThat(response.getHasPassword()).isTrue();
        assertThat(response.getHostProfile().getUserId()).isEqualTo(user.getId());
        assertThat(response.getHostProfile().getIsHost()).isTrue();
    }

    @Test
    @DisplayName("채팅방 좋아요 상태 조회 성공")
    void getChatroomLikeSuccess() {
        String username = "testUser";
        User user = User.builder().username(username).build();

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(likeService.getLikeStatus(user, chatroom)).thenReturn(true);
        when(likeService.getLikeCount(chatroom)).thenReturn(3L);

        ChatroomLikeStatusResponse result = chatFacade.getChatroomLike(username, chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.getIsLiked()).isTrue();
        assertThat(result.getLikeCount()).isEqualTo(3L);
    }
}
