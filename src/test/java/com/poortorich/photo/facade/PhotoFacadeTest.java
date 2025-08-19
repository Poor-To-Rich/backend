package com.poortorich.photo.facade;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.photo.request.PhotoUploadRequest;
import com.poortorich.photo.response.PhotoUploadResponse;
import com.poortorich.photo.response.enums.PhotoResponse;
import com.poortorich.photo.service.PhotoService;
import com.poortorich.s3.service.FileUploadService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhotoFacadeTest {

    @Mock
    private UserService userService;
    @Mock
    private ChatroomService chatroomService;
    @Mock
    private FileUploadService fileUploadService;
    @Mock
    private PhotoService photoService;
    @Mock
    private ChatParticipantValidator chatParticipantValidator;

    @InjectMocks
    private PhotoFacade photoFacade;

    @Test
    @DisplayName("채팅방 이미지 업로드 성공")
    void uploadPhotoSuccess() {
        String username = "test";
        Long chatroomId = 1L;
        String photoUrl = "https://photo.com";
        User user = User.builder().username(username).build();
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();
        PhotoUploadRequest request = new PhotoUploadRequest(Mockito.mock(MultipartFile.class));

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(fileUploadService.uploadImage(request.getPhoto())).thenReturn(photoUrl);

        PhotoUploadResponse response = photoFacade.uploadPhoto(username, chatroomId, request);

        verify(photoService).savePhoto(user, chatroom, photoUrl);
        assertThat(response.getPhotoUrl()).isEqualTo(photoUrl);
    }

    @Test
    @DisplayName("이미지가 null인 경우 예외 발생")
    void uploadPhotoNull() {
        String username = "test";
        Long chatroomId = 1L;
        PhotoUploadRequest request = new PhotoUploadRequest(null);

        assertThatThrownBy(() -> photoFacade.uploadPhoto(username, chatroomId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(PhotoResponse.PHOTO_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("이미지가 비어있는 경우 예외 발생")
    void uploadPhotoEmpty() {
        String username = "test";
        Long chatroomId = 1L;

        MultipartFile emptyFile = Mockito.mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);
        PhotoUploadRequest request = new PhotoUploadRequest(emptyFile);

        assertThatThrownBy(() -> photoFacade.uploadPhoto(username, chatroomId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(PhotoResponse.PHOTO_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("채팅방에 참여중이 아닌 경우 예외 발생")
    void uploadPhotoNotParticipate() {
        String username = "test";
        Long chatroomId = 1L;
        User user = User.builder().username(username).build();
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();

        MultipartFile file = Mockito.mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        PhotoUploadRequest request = new PhotoUploadRequest(file);

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);

        doThrow(new BadRequestException(ChatResponse.CHATROOM_NOT_PARTICIPATE))
                .when(chatParticipantValidator).validateIsParticipate(user, chatroom);

        assertThatThrownBy(() -> photoFacade.uploadPhoto(username, chatroomId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(ChatResponse.CHATROOM_NOT_PARTICIPATE.getMessage());

        verify(chatParticipantValidator).validateIsParticipate(user, chatroom);
        verifyNoInteractions(fileUploadService, photoService);
    }
}
