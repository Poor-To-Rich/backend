package com.poortorich.photo.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.photo.entity.Photo;
import com.poortorich.photo.repository.PhotoRepository;
import com.poortorich.photo.response.enums.PhotoResponse;
import com.poortorich.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @InjectMocks
    private PhotoService photoService;

    @Captor
    private ArgumentCaptor<Photo> captor;

    private final Pageable pageable = PageRequest.of(0, 20);

    @Test
    @DisplayName("채팅방 이미지 저장 성공")
    void savePhotoSuccess() {
        User user = User.builder().build();
        Chatroom chatroom = Chatroom.builder().build();
        String photoUrl = "https://photo.com";

        photoService.savePhoto(user, chatroom, photoUrl);

        verify(photoRepository).save(captor.capture());
        Photo savedPhoto = captor.getValue();

        assertThat(savedPhoto.getUser()).isEqualTo(user);
        assertThat(savedPhoto.getChatroom()).isEqualTo(chatroom);
        assertThat(savedPhoto.getPhotoUrl()).isEqualTo(photoUrl);
    }

    @Test
    @DisplayName("최신 사진 목록 조회 성공")
    void getPreviewPhotosSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        Photo photo1 = Photo.builder().build();
        Photo photo2 = Photo.builder().build();
        Photo photo3 = Photo.builder().build();
        Photo photo4 = Photo.builder().build();
        Photo photo5 = Photo.builder().build();

        when(photoRepository.findTop10ByChatroomOrderByCreatedDateDescIdAsc(chatroom))
                .thenReturn(List.of(photo1, photo2, photo3, photo4, photo5));

        List<Photo> result = photoService.getPreviewPhotos(chatroom);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(5);
        assertThat(result).containsExactly(photo1, photo2, photo3, photo4, photo5);
    }

    @Test
    @DisplayName("전체 사진 목록 조회 성공")
    void getAllPhotosByCursorSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        LocalDateTime date = LocalDateTime.of(2025, 8, 26, 0, 0, 0);
        Long photoId = 99L;

        Photo photo1 = Photo.builder().id(1L).photoUrl("photo1.com").build();
        Photo photo2 = Photo.builder().id(2L).photoUrl("photo2.com").build();
        Photo photo3 = Photo.builder().id(3L).photoUrl("photo3.com").build();

        SliceImpl<Photo> photoSlice = new SliceImpl<>(List.of(photo3, photo2, photo1), pageable, true);

        when(photoRepository.findAllByChatroomAndCursor(chatroom, date, photoId, pageable))
                .thenReturn(photoSlice);

        Slice<Photo> result = photoService.getAllPhotosByCursor(chatroom, date, photoId, pageable);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(photo3, photo2, photo1);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("채팅방과 사진 아이디로 사진 조회 성공")
    void findByChatroomAndIdSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        Long photoId = 1L;
        Photo photo = Photo.builder()
                .id(photoId)
                .chatroom(chatroom)
                .build();

        when(photoRepository.findByChatroomAndId(chatroom, photoId)).thenReturn(Optional.of(photo));

        Photo result = photoService.findByChatroomAndId(chatroom, photoId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(photo);
        assertThat(result.getId()).isEqualTo(photoId);
    }

    @Test
    @DisplayName("채팅방과 사진 아이디로 사진 조회 실패")
    void findByChatroomAndIdFail() {
        Chatroom chatroom = Chatroom.builder().build();
        Long photoId = 1L;

        when(photoRepository.findByChatroomAndId(chatroom, photoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> photoService.findByChatroomAndId(chatroom, photoId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(PhotoResponse.PHOTO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("이전 사진 아이디 조회 성공")
    void findPrevPhotoIdSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        Long prevPhotoId = 1L;
        Long currentPhotoId = 2L;
        LocalDateTime createdDate = LocalDateTime.of(2025, 8, 26, 0, 0, 0);
        Photo currentPhoto = Photo.builder()
                .id(currentPhotoId)
                .chatroom(chatroom)
                .createdDate(createdDate)
                .build();

        when(photoRepository.findPrevPhotoId(chatroom, createdDate, currentPhotoId)).thenReturn(List.of(prevPhotoId));

        Long result = photoService.findPrevPhotoId(chatroom, currentPhoto);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(prevPhotoId);
    }

    @Test
    @DisplayName("다음 사진 아이디 조회 성공")
    void findNextPhotoIdSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        Long nextPhotoId = 3L;
        Long currentPhotoId = 2L;
        LocalDateTime createdDate = LocalDateTime.of(2025, 8, 26, 0, 0, 0);
        Photo currentPhoto = Photo.builder()
                .id(currentPhotoId)
                .chatroom(chatroom)
                .createdDate(createdDate)
                .build();

        when(photoRepository.findNextPhotoId(chatroom, createdDate, currentPhotoId)).thenReturn(List.of(nextPhotoId));

        Long result = photoService.findNextPhotoId(chatroom, currentPhoto);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(nextPhotoId);
    }
}
