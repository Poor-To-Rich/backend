package com.poortorich.photo.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.photo.entity.Photo;
import com.poortorich.photo.repository.PhotoRepository;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    public void savePhoto(User user, Chatroom chatroom, String photoUrl) {
        Photo photo = Photo.builder()
                .user(user)
                .chatroom(chatroom)
                .photoUrl(photoUrl)
                .build();
        photoRepository.save(photo);
    }

    public List<Photo> getPreviewPhotos(Chatroom chatroom) {
        return photoRepository.findTop10ByChatroomOrderByCreatedDateDescIdAsc(chatroom);
    }
}
