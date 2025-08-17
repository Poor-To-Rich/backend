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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @InjectMocks
    private PhotoService photoService;

    @Captor
    private ArgumentCaptor<Photo> captor;

    @Test
    @DisplayName("채팅방 이미지 저장 성공")
    void uploadPhotoSuccess() {
        User user = User.builder().build();
        Chatroom chatroom = Chatroom.builder().build();
        String photoUrl = "https://photo.com";

        photoService.uploadPhoto(user, chatroom, photoUrl);

        verify(photoRepository).save(captor.capture());
        Photo savedPhoto = captor.getValue();

        assertThat(savedPhoto.getUser()).isEqualTo(user);
        assertThat(savedPhoto.getChatroom()).isEqualTo(chatroom);
        assertThat(savedPhoto.getPhotoUrl()).isEqualTo(photoUrl);
    }
}