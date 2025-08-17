package com.poortorich.photo.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.photo.entity.Photo;
import com.poortorich.photo.repository.PhotoRepository;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    public void uploadPhoto(User user, Chatroom chatroom, String photoUrl) {
        Photo photo = Photo.builder()
                .user(user)
                .chatroom(chatroom)
                .photoUrl(photoUrl)
                .build();
        photoRepository.save(photo);
    }
}
