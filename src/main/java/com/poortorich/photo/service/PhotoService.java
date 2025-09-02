package com.poortorich.photo.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.photo.entity.Photo;
import com.poortorich.photo.repository.PhotoRepository;
import com.poortorich.photo.response.enums.PhotoResponse;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final FileUploadService fileUploadService;

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

    public Slice<Photo> getAllPhotosByCursor(Chatroom chatroom, LocalDateTime date, Long id, Pageable pageable) {
        return photoRepository.findAllByChatroomAndCursor(chatroom, date, id, pageable);
    }

    public Photo findByChatroomAndId(Chatroom chatroom, Long photoId) {
        return photoRepository.findByChatroomAndId(chatroom, photoId)
                .orElseThrow(() -> new BadRequestException(PhotoResponse.PHOTO_NOT_FOUND));
    }

    public Long findPrevPhotoId(Chatroom chatroom, Photo currentPhoto) {
        return photoRepository.findPrevPhotoId(chatroom, currentPhoto.getCreatedDate(), currentPhoto.getId()).stream()
                .findFirst()
                .orElse(null);
    }

    public Long findNextPhotoId(Chatroom chatroom, Photo currentPhoto) {
        return photoRepository.findNextPhotoId(chatroom, currentPhoto.getCreatedDate(), currentPhoto.getId()).stream()
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void deleteAllByChatroom(Chatroom chatroom) {
        List<Photo> photos = photoRepository.findAllByChatroom(chatroom);
        photos.forEach(photo -> fileUploadService.deleteImage(photo.getPhotoUrl()));
        photoRepository.deleteByChatroom(chatroom);
    }
}
