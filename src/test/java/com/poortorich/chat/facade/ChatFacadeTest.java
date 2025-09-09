package com.poortorich.chat.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.request.enums.SortBy;
import com.poortorich.chat.response.AllChatroomsResponse;
import com.poortorich.chat.response.AllParticipantsResponse;
import com.poortorich.chat.response.ChatParticipantProfile;
import com.poortorich.chat.response.ChatroomCoverInfoResponse;
import com.poortorich.chat.response.ChatroomCreateResponse;
import com.poortorich.chat.response.ChatroomDetailsResponse;
import com.poortorich.chat.response.ChatroomInfoResponse;
import com.poortorich.chat.response.ChatroomResponse;
import com.poortorich.chat.response.ChatroomRoleResponse;
import com.poortorich.chat.response.ChatroomsResponse;
import com.poortorich.chat.response.SearchParticipantsResponse;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.chat.util.mapper.ParticipantProfileMapper;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.chatnotice.request.ChatNoticeStatusUpdateRequest;
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

    private final Long chatroomId = 1L;
    private final MultipartFile image = Mockito.mock(MultipartFile.class);
    private final String imageUrl = "https://image.com";
    private final String chatroomTitle = "채팅방";
    private final Long maxMemberCount = 10L;
    private final List<String> hashtags = List.of("부자", "거지");
    private final Boolean isRankingEnabled = false;
    private final String chatroomPassword = "부자12";
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
    private ChatParticipantValidator chatParticipantValidator;
    @Mock
    private ParticipantProfileMapper participantProfileMapper;
    @Mock
    private ChatBuilder chatBuilder;

    @InjectMocks
    private ChatFacade chatFacade;
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
                .isClosed(false)
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
    @DisplayName("해시태그가 없는 채팅방 추가 성공")
    void createChatroomTagNullSuccess() {
        String username = "test";
        ChatroomCreateRequest request = new ChatroomCreateRequest(
                image, chatroomTitle, maxMemberCount, null, null, isRankingEnabled, chatroomPassword
        );
        User user = User.builder().username(username).build();

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(fileUploadService.uploadImage(image)).thenReturn(imageUrl);
        when(chatroomService.createChatroom(imageUrl, request)).thenReturn(chatroom);

        ChatroomCreateResponse response = chatFacade.createChatroom(username, request);

        verify(chatParticipantService).createChatroomHost(user, chatroom);

        assertThat(response.getNewChatroomId()).isEqualTo(chatroom.getId());
    }

    @Test
    @DisplayName("채팅방 정보 조회 성공")
    void getChatroomSuccess() {
        ChatroomInfoResponse expectedResponse = ChatroomInfoResponse.builder()
                .chatroomImage(chatroom.getImage())
                .chatroomTitle(chatroom.getTitle())
                .maxMemberCount(chatroom.getMaxMemberCount())
                .isRankingEnabled(chatroom.getIsRankingEnabled())
                .chatroomPassword(chatroom.getPassword())
                .hashtags(hashtags)
                .build();

        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(tagService.getTagNames(chatroom)).thenReturn(hashtags);
        when(chatBuilder.buildChatroomInfoResponse(chatroom, hashtags)).thenReturn(expectedResponse);

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
        when(chatBuilder.buildChatroomResponse(chatroom, hashtags, 1L, "2025-07-31T02:30"))
                .thenReturn(ChatroomResponse.builder()
                        .chatroomId(chatroom.getId())
                        .chatroomTitle(chatroom.getTitle())
                        .chatroomImage(chatroom.getImage())
                        .description(chatroom.getDescription())
                        .hashtags(hashtags)
                        .currentMemberCount(1L)
                        .maxMemberCount(chatroom.getMaxMemberCount())
                        .lastMessageTime("2025-07-31T02:30")
                        .build());

        when(chatBuilder.buildChatroomResponse(chatroom2, hashtags, 2L, "2025-07-31T03:00"))
                .thenReturn(ChatroomResponse.builder()
                        .chatroomId(chatroom2.getId())
                        .chatroomTitle(chatroom2.getTitle())
                        .chatroomImage(chatroom2.getImage())
                        .description(chatroom2.getDescription())
                        .hashtags(hashtags)
                        .currentMemberCount(2L)
                        .maxMemberCount(chatroom2.getMaxMemberCount())
                        .lastMessageTime("2025-07-31T03:00")
                        .build());
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
        when(chatBuilder.buildChatroomResponse(chatroom1, hashtags, 3L, "2025-07-31T02:30"))
                .thenReturn(ChatroomResponse.builder()
                        .chatroomId(chatroom1.getId())
                        .chatroomTitle(chatroom1.getTitle())
                        .chatroomImage(chatroom1.getImage())
                        .description(chatroom1.getDescription())
                        .hashtags(hashtags)
                        .currentMemberCount(3L)
                        .maxMemberCount(chatroom1.getMaxMemberCount())
                        .lastMessageTime("2025-07-31T02:30")
                        .build());

        when(chatBuilder.buildChatroomResponse(chatroom2, hashtags, 3L, "2025-07-31T02:30"))
                .thenReturn(ChatroomResponse.builder()
                        .chatroomId(chatroom2.getId())
                        .chatroomTitle(chatroom2.getTitle())
                        .chatroomImage(chatroom2.getImage())
                        .description(chatroom2.getDescription())
                        .hashtags(hashtags)
                        .currentMemberCount(3L)
                        .maxMemberCount(chatroom2.getMaxMemberCount())
                        .lastMessageTime("2025-07-31T02:30")
                        .build());
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
        when(chatBuilder.buildChatroomResponse(chatroom1, hashtags, 3L, "2025-07-31T02:30"))
                .thenReturn(ChatroomResponse.builder()
                        .chatroomId(chatroom1.getId())
                        .chatroomTitle(chatroom1.getTitle())
                        .chatroomImage(chatroom1.getImage())
                        .description(chatroom1.getDescription())
                        .hashtags(hashtags)
                        .currentMemberCount(3L)
                        .maxMemberCount(chatroom1.getMaxMemberCount())
                        .lastMessageTime("2025-07-31T02:30")
                        .build());

        when(chatBuilder.buildChatroomResponse(chatroom2, hashtags, 3L, "2025-07-31T02:30"))
                .thenReturn(ChatroomResponse.builder()
                        .chatroomId(chatroom2.getId())
                        .chatroomTitle(chatroom2.getTitle())
                        .chatroomImage(chatroom2.getImage())
                        .description(chatroom2.getDescription())
                        .hashtags(hashtags)
                        .currentMemberCount(3L)
                        .maxMemberCount(chatroom2.getMaxMemberCount())
                        .lastMessageTime("2025-07-31T02:30")
                        .build());
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
        when(chatBuilder.buildChatroomDetailsResponse(chatroom, 5L))
                .thenReturn(ChatroomDetailsResponse.builder()
                        .chatroomImage(chatroom.getImage())
                        .chatroomTitle(chatroom.getTitle())
                        .currentMemberCount(5L)
                        .isRankingEnabled(chatroom.getIsRankingEnabled())
                        .isClosed(chatroom.getIsClosed())
                        .build());
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
        ChatParticipantProfile hostProfile = ChatParticipantProfile.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .rankingType(hostParticipant.getRankingStatus())
                .isHost(true)
                .build();
        when(userService.findUserByUsername(username)).thenReturn(user);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(tagService.getTagNames(chatroom)).thenReturn(hashtags);
        when(chatParticipantService.countByChatroom(chatroom)).thenReturn(3L);
        when(chatParticipantService.isJoined(user, chatroom)).thenReturn(true);
        when(chatParticipantService.getChatroomHost(chatroom)).thenReturn(hostParticipant);
        when(chatBuilder.buildChatroomCoverInfoResponse(
                chatroom,
                hashtags,
                3L,
                true,
                hostParticipant
        )).thenReturn(ChatroomCoverInfoResponse.builder()
                .chatroomId(chatroom.getId())
                .chatroomTitle(chatroom.getTitle())
                .chatroomImage(chatroom.getImage())
                .description(chatroom.getDescription())
                .hashtags(hashtags)
                .currentMemberCount(3L)
                .maxMemberCount(chatroom.getMaxMemberCount())
                .createdAt(chatroom.getCreatedDate().toString())
                .isJoined(true)
                .hasPassword(true)
                .hostProfile(hostProfile)
                .build());
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
    @DisplayName("채팅방 내 사용자 역할 조회 성공")
    void getChatroomRoleSuccess() {
        String username = "testUser";
        Long chatroomId = 1L;
        User user = User.builder()
                .id(1L)
                .username(username)
                .build();
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .user(user)
                .role(ChatroomRole.HOST)
                .build();

        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatParticipantService.findByUsernameAndChatroom(username, chatroom)).thenReturn(chatParticipant);

        ChatroomRoleResponse result = chatFacade.getChatroomRole(username, chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.getChatroomRole()).isEqualTo(ChatroomRole.HOST.toString());
        assertThat(result.getUserId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("공지 상태 변경 성공")
    void updateNoticeStatusSuccess() {
        String username = "testUser";
        Long chatroomId = 1L;
        ChatNoticeStatusUpdateRequest request = new ChatNoticeStatusUpdateRequest("DEFAULT");

        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);

        chatFacade.updateNoticeStatus(username, chatroomId, request);

        verify(chatParticipantService).updateNoticeStatus(username, chatroom, request);
    }

    @Test
    @DisplayName("전체 참여 인원 목록 조회 성공")
    void getAllParticipantsSuccess() {
        String username = "host";
        User hostUser = User.builder()
                .id(1L)
                .profileImage("profileImage.com")
                .username(username)
                .nickname(username)
                .build();
        ChatParticipant host = ChatParticipant.builder()
                .user(hostUser)
                .chatroom(chatroom)
                .role(ChatroomRole.HOST)
                .rankingStatus(RankingStatus.NONE)
                .build();
        User memberUser = User.builder().id(2L).profileImage("profileImage.com").nickname("member1").build();
        ChatParticipant member1 = ChatParticipant.builder()
                .user(memberUser)
                .chatroom(chatroom)
                .role(ChatroomRole.MEMBER)
                .rankingStatus(RankingStatus.NONE)
                .build();
        AllParticipantsResponse expectedResponse = AllParticipantsResponse.builder()
                .memberCount(2L)
                .members(List.of(
                        ChatParticipantProfile.builder()
                                .userId(hostUser.getId())
                                .nickname(hostUser.getNickname())
                                .profileImage(hostUser.getProfileImage())
                                .rankingType(host.getRankingStatus())
                                .isHost(true)
                                .build(),
                        ChatParticipantProfile.builder()
                                .userId(memberUser.getId())
                                .nickname(memberUser.getNickname())
                                .profileImage(memberUser.getProfileImage())
                                .rankingType(member1.getRankingStatus())
                                .isHost(false)
                                .build()
                ))
                .build();
        when(userService.findUserByUsername(username)).thenReturn(hostUser);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatParticipantService.getAllParticipants(chatroom)).thenReturn(List.of(host, member1));
        when(chatBuilder.buildAllParticipantsResponse(List.of(host, member1)))
                .thenReturn(expectedResponse);
        AllParticipantsResponse response = chatFacade.getAllParticipants(username, chatroomId);

        assertThat(response).isNotNull();
        assertThat(response.getMemberCount()).isEqualTo(2L);
        assertThat(response.getMembers().get(0).getUserId()).isEqualTo(hostUser.getId());
        assertThat(response.getMembers().get(0).getNickname()).isEqualTo(hostUser.getNickname());
        assertThat(response.getMembers().get(0).getIsHost()).isTrue();
        assertThat(response.getMembers().get(1).getUserId()).isEqualTo(memberUser.getId());
        assertThat(response.getMembers().get(1).getNickname()).isEqualTo(memberUser.getNickname());
        assertThat(response.getMembers().get(1).getIsHost()).isFalse();
    }

    @Test
    @DisplayName("참여 인원 검색 성공")
    void searchParticipantsByNicknameSuccess() {
        String username = "testUser";
        Long chatroomId = 1L;
        String keyword = "nick";
        User user = User.builder().username(username).build();

        User user1 = User.builder()
                .id(1L)
                .profileImage("profileImage.com")
                .nickname("nick1")
                .build();
        User user2 = User.builder()
                .id(2L)
                .profileImage("profileImage.com")
                .nickname("nick2")
                .build();
        User user3 = User.builder()
                .id(3L)
                .profileImage("profileImage.com")
                .nickname("nick3")
                .build();

        ChatParticipant member1 = ChatParticipant.builder()
                .user(user1)
                .chatroom(chatroom)
                .role(ChatroomRole.MEMBER)
                .rankingStatus(RankingStatus.NONE)
                .build();
        ChatParticipant member2 = ChatParticipant.builder()
                .user(user2)
                .chatroom(chatroom)
                .role(ChatroomRole.MEMBER)
                .rankingStatus(RankingStatus.NONE)
                .build();
        ChatParticipant member3 = ChatParticipant.builder()
                .user(user3)
                .chatroom(chatroom)
                .role(ChatroomRole.MEMBER)
                .rankingStatus(RankingStatus.NONE)
                .build();
        ChatParticipantProfile profile1 = ChatParticipantProfile.builder()
                .userId(1L)
                .nickname("nick1")
                .profileImage("profileImage.com")
                .rankingType(RankingStatus.NONE)
                .isHost(false)
                .build();

        ChatParticipantProfile profile2 = ChatParticipantProfile.builder()
                .userId(2L)
                .nickname("nick2")
                .profileImage("profileImage.com")
                .rankingType(RankingStatus.NONE)
                .isHost(false)
                .build();

        ChatParticipantProfile profile3 = ChatParticipantProfile.builder()
                .userId(3L)
                .nickname("nick3")
                .profileImage("profileImage.com")
                .rankingType(RankingStatus.NONE)
                .isHost(false)
                .build();
        when(userService.findUserByUsername(username)).thenReturn(user);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatParticipantService.searchParticipantsByNickname(chatroom, keyword))
                .thenReturn(List.of(member1, member2, member3));
        when(participantProfileMapper.mapToProfile(member1)).thenReturn(profile1);
        when(participantProfileMapper.mapToProfile(member2)).thenReturn(profile2);
        when(participantProfileMapper.mapToProfile(member3)).thenReturn(profile3);
        SearchParticipantsResponse result = chatFacade.searchParticipantsByNickname(username, chatroomId, keyword);

        assertThat(result).isNotNull();
        assertThat(result.getMembers()).hasSize(3);
        assertThat(result.getMembers().get(0).getUserId()).isEqualTo(user1.getId());
        assertThat(result.getMembers().get(0).getNickname()).isEqualTo(user1.getNickname());
        assertThat(result.getMembers().get(1).getUserId()).isEqualTo(user2.getId());
        assertThat(result.getMembers().get(1).getNickname()).isEqualTo(user2.getNickname());
        assertThat(result.getMembers().get(2).getUserId()).isEqualTo(user3.getId());
        assertThat(result.getMembers().get(2).getNickname()).isEqualTo(user3.getNickname());
    }
}
