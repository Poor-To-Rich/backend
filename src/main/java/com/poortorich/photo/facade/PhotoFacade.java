package com.poortorich.photo.facade;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.global.date.constants.DatePattern;
import com.poortorich.global.date.util.DateParser;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.photo.entity.Photo;
import com.poortorich.photo.request.PhotoUploadRequest;
import com.poortorich.photo.response.AllPhotosResponse;
import com.poortorich.photo.response.PhotoDetailsResponse;
import com.poortorich.photo.response.PhotoUploadResponse;
import com.poortorich.photo.response.PreviewPhotosResponse;
import com.poortorich.photo.response.enums.PhotoResponse;
import com.poortorich.photo.service.PhotoService;
import com.poortorich.photo.util.PhotoBuilder;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PhotoFacade {

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final FileUploadService fileUploadService;
    private final PhotoService photoService;
    private final ChatParticipantValidator chatParticipantValidator;

    @Transactional
    public PhotoUploadResponse uploadPhoto(String username, Long chatroomId, PhotoUploadRequest request) {
        validatePhoto(request.getPhoto());

        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        chatParticipantValidator.validateIsParticipate(user, chatroom);
        String photoUrl = fileUploadService.uploadImage(request.getPhoto());

        photoService.savePhoto(user, chatroom, photoUrl);

        return PhotoUploadResponse.builder().photoUrl(photoUrl).build();
    }

    private void validatePhoto(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new BadRequestException(PhotoResponse.PHOTO_REQUIRED);
        }
    }

    @Transactional(readOnly = true)
    public PreviewPhotosResponse getPreviewPhotos(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        chatParticipantValidator.validateIsParticipate(user, chatroom);

        return PhotoBuilder.buildPhotosResponse(photoService.getPreviewPhotos(chatroom));
    }

    @Transactional(readOnly = true)
    public AllPhotosResponse getAllPhotosByCursor(String username, Long chatroomId, String date, Long id) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        chatParticipantValidator.validateIsParticipate(user, chatroom);

        LocalDateTime cursorDate = DateParser.parseDateTime(date);
        Pageable pageable = PageRequest.of(0, 20);
        Slice<Photo> photos = photoService.getAllPhotosByCursor(chatroom, cursorDate, id, pageable);

        String nextDate = photos.getContent().getLast().getCreatedDate()
                .format(DateTimeFormatter.ofPattern(DatePattern.LOCAL_DATE_TIME_PATTERN));

        return PhotoBuilder.buildAllPhotosResponse(
                photos.hasNext(),
                photos.hasNext() ? nextDate : null,
                photos.hasNext() ? photos.getContent().getLast().getId() : null,
                photos.getContent()
        );
    }

    @Transactional(readOnly = true)
    public PhotoDetailsResponse getPhotoDetails(String username, Long chatroomId, Long photoId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        chatParticipantValidator.validateIsParticipate(user, chatroom);

        Photo photo = photoService.findByChatroomAndId(chatroom, photoId);

        return PhotoBuilder.buildPhotoDetailsResponse(
                photo,
                photoService.findPrevPhotoId(chatroom, photo),
                photoService.findNextPhotoId(chatroom, photo)
        );
    }
}
