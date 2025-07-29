package com.poortorich.chat.facade;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.response.ChatroomCreateResponse;
import com.poortorich.chat.response.ChatroomInfoResponse;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
                .build();
    }

    @Test
    @DisplayName("채팅방 추가 성공")
    void createChatroomSuccess() {
        String username = "test";
        ChatroomCreateRequest request = new ChatroomCreateRequest(
                chatroomTitle, maxMemberCount, null, hashtags, isRankingEnabled, chatroomPassword
        );
        User user = User.builder().username(username).build();

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(fileUploadService.uploadImage(image)).thenReturn(imageUrl);
        when(chatroomService.createChatroom(imageUrl, request)).thenReturn(chatroom);

        ChatroomCreateResponse response = chatFacade.createChatroom(username, image, request);

        verify(chatParticipantService).createChatroomHost(user, chatroom);
        verify(tagService).createTag(hashtags, chatroom);

        assertThat(response.getNewChatroomId()).isEqualTo(chatroom.getId());
    }

    @Test
    @DisplayName("채팅방 정보 조회 성공")
    void getChatroomSuccess() {
        when(chatroomService.findChatroomById(chatroomId)).thenReturn(chatroom);
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
}
