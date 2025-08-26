package com.poortorich.photo.facade;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.global.date.util.DateParser;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.photo.entity.Photo;
import com.poortorich.photo.request.PhotoUploadRequest;
import com.poortorich.photo.response.AllPhotosResponse;
import com.poortorich.photo.response.PhotoUploadResponse;
import com.poortorich.photo.response.PreviewPhotosResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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

    @Test
    @DisplayName("최신 사진 목록 조회 성공")
    void getPreviewPhotosSuccess() {
        String username = "test";
        Long chatroomId = 1L;
        User user = User.builder().username(username).build();
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();
        Photo photo1 = Photo.builder()
                .id(1L)
                .user(user)
                .chatroom(chatroom)
                .photoUrl("photo1.com")
                .createdDate(LocalDateTime.of(2025, 1, 3, 0, 0, 0))
                .build();
        Photo photo2 = Photo.builder()
                .id(2L)
                .user(user)
                .chatroom(chatroom)
                .photoUrl("photo2.com")
                .createdDate(LocalDateTime.of(2025, 1, 2, 0, 0, 0))
                .build();
        Photo photo3 = Photo.builder()
                .id(3L)
                .user(user)
                .chatroom(chatroom)
                .photoUrl("photo3.com")
                .createdDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
                .build();

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(photoService.getPreviewPhotos(chatroom)).thenReturn(List.of(photo1, photo2, photo3));

        PreviewPhotosResponse result = photoFacade.getPreviewPhotos(username, chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.getPhotos()).hasSize(3);
        assertThat(result.getPhotos().get(0).getPhotoId()).isEqualTo(photo1.getId());
        assertThat(result.getPhotos().get(0).getPhotoUrl()).isEqualTo(photo1.getPhotoUrl());
        assertThat(result.getPhotos().get(1).getPhotoId()).isEqualTo(photo2.getId());
        assertThat(result.getPhotos().get(1).getPhotoUrl()).isEqualTo(photo2.getPhotoUrl());
        assertThat(result.getPhotos().get(2).getPhotoId()).isEqualTo(photo3.getId());
        assertThat(result.getPhotos().get(2).getPhotoUrl()).isEqualTo(photo3.getPhotoUrl());
    }

    @Test
    @DisplayName("전체 사진 목록 조회 성공")
    void getAllPhotosByCursorSuccess() {
        String username = "test";
        Long chatroomId = 1L;
        String dateString = "20250826010000";
        LocalDateTime date = DateParser.parseDateTime(dateString);
        Long photoId = 99L;
        Pageable pageable = PageRequest.of(0, 20);
        User user = User.builder().username(username).build();
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();
        Photo photo1 = Photo.builder()
                .id(1L)
                .user(user)
                .chatroom(chatroom)
                .photoUrl("photo1.com")
                .createdDate(LocalDateTime.of(2025, 8, 20, 0, 0, 0))
                .build();
        Photo photo2 = Photo.builder()
                .id(2L)
                .user(user)
                .chatroom(chatroom)
                .photoUrl("photo2.com")
                .createdDate(LocalDateTime.of(2025, 8, 19, 0, 0, 0))
                .build();
        Photo photo3 = Photo.builder()
                .id(3L)
                .user(user)
                .chatroom(chatroom)
                .photoUrl("photo3.com")
                .createdDate(LocalDateTime.of(2025, 8, 18, 0, 0, 0))
                .build();
        Slice<Photo> slice = new SliceImpl<>(List.of(photo3, photo2, photo1), pageable, true);

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(photoService.getAllPhotosByCursor(chatroom, date, photoId, pageable)).thenReturn(slice);

        AllPhotosResponse result = photoFacade.getAllPhotosByCursor(username, chatroomId, dateString, photoId);

        assertThat(result).isNotNull();
        assertThat(result.getHasNext()).isTrue();
        assertThat(result.getNextId()).isEqualTo(slice.getContent().getLast().getId());
        assertThat(result.getPhotos()).hasSize(3);
        assertThat(result.getPhotos().get(0).getPhotoId()).isEqualTo(photo3.getId());
        assertThat(result.getPhotos().get(0).getPhotoUrl()).isEqualTo(photo3.getPhotoUrl());
        assertThat(result.getPhotos().get(1).getPhotoId()).isEqualTo(photo2.getId());
        assertThat(result.getPhotos().get(1).getPhotoUrl()).isEqualTo(photo2.getPhotoUrl());
        assertThat(result.getPhotos().get(2).getPhotoId()).isEqualTo(photo1.getId());
        assertThat(result.getPhotos().get(2).getPhotoUrl()).isEqualTo(photo1.getPhotoUrl());
    }
}
