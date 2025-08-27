package com.poortorich.photo.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.photo.entity.Photo;
import com.poortorich.photo.repository.PhotoRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
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
}
