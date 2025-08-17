package com.poortorich.photo.facade;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.photo.request.PhotoUploadRequest;
import com.poortorich.photo.response.PhotoUploadResponse;
import com.poortorich.photo.response.enums.PhotoResponse;
import com.poortorich.photo.service.PhotoService;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PhotoFacade {

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final FileUploadService fileUploadService;
    private final PhotoService photoService;

    @Transactional
    public PhotoUploadResponse uploadPhoto(String username, Long chatroomId, PhotoUploadRequest request) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        validatePhoto(request.getPhoto());
        String photoUrl = fileUploadService.uploadImage(request.getPhoto());

        photoService.uploadPhoto(user, chatroom, photoUrl);

        return PhotoUploadResponse.builder().photoUrl(photoUrl).build();
    }

    private void validatePhoto(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new BadRequestException(PhotoResponse.PHOTO_REQUIRED);
        }
    }
}
