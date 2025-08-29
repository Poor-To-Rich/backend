package com.poortorich.photo.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.photo.facade.PhotoFacade;
import com.poortorich.photo.request.PhotoUploadRequest;
import com.poortorich.photo.response.enums.PhotoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatrooms/{chatroomId}/photos")
@RequiredArgsConstructor
public class PhotoController {

    private static final String ID_DEFAULT = "" + Long.MAX_VALUE;

    private final PhotoFacade photoFacade;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> uploadPhoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            PhotoUploadRequest request
    ) {
        return DataResponse.toResponseEntity(
                PhotoResponse.UPLOAD_PHOTO_SUCCESS,
                photoFacade.uploadPhoto(userDetails.getUsername(), chatroomId, request)
        );
    }

    @GetMapping("/preview")
    public ResponseEntity<BaseResponse> getPreviewPhotos(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId
    ) {
        return DataResponse.toResponseEntity(
                PhotoResponse.GET_PREVIEW_PHOTOS_SUCCESS,
                photoFacade.getPreviewPhotos(userDetails.getUsername(), chatroomId)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse> getAllPhotosByCursor(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @RequestParam(defaultValue = "") String date,
            @RequestParam(defaultValue = ID_DEFAULT) Long id
    ) {
        return DataResponse.toResponseEntity(
                PhotoResponse.GET_ALL_PHOTOS_SUCCESS,
                photoFacade.getAllPhotosByCursor(userDetails.getUsername(), chatroomId, date, id)
        );
    }

    @GetMapping("/{photoId}")
    public ResponseEntity<BaseResponse> getPhotoDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @PathVariable Long photoId
    ) {
        return DataResponse.toResponseEntity(
                PhotoResponse.GET_PHOTO_DETAILS_SUCCESS,
                photoFacade.getPhotoDetails(userDetails.getUsername(), chatroomId, photoId)
        );
    }
}
