package com.poortorich.chat.facade;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.response.ChatroomCreateResponse;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.tag.service.TagService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
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

    @Test
    @DisplayName("채팅방 추가 성공")
    void createChatroomSuccess() {
        String username = "test";
        MultipartFile image = Mockito.mock(MultipartFile.class);
        String imageUrl = "https://image.com";
        String chatroomTitle = "채팅방";
        Long maxMemberCount = 10L;
        List<String> hashtags = List.of("부자", "거지");
        Boolean isRankingEnabled = false;
        String chatroomPassword = "부자12";
        ChatroomCreateRequest request = new ChatroomCreateRequest(
                image, chatroomTitle, maxMemberCount, null, hashtags, isRankingEnabled, chatroomPassword
        );
        User user = User.builder().username(username).build();
        Chatroom chatroom = Chatroom.builder().id(1L).title(chatroomTitle).build();

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(fileUploadService.uploadImage(image)).thenReturn(imageUrl);
        when(chatroomService.createChatroom(imageUrl, request)).thenReturn(chatroom);

        ChatroomCreateResponse response = chatFacade.createChatroom(username, request);

        verify(chatParticipantService).createChatroomHost(user, chatroom);
        verify(tagService).createTag(hashtags, chatroom);

        assertThat(response.getNewChatroomId()).isEqualTo(chatroom.getId());
    }
}
