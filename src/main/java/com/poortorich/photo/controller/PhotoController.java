package com.poortorich.photo.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.photo.facade.PhotoFacade;
import com.poortorich.photo.request.PhotoUploadRequest;
import com.poortorich.photo.response.enums.PhotoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatrooms/{chatroomId}/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoFacade photoFacade;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> uploadPhoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @Valid PhotoUploadRequest request
    ) {
        return DataResponse.toResponseEntity(
                PhotoResponse.UPLOAD_PHOTO_SUCCESS,
                photoFacade.uploadPhoto(userDetails.getUsername(), chatroomId, request)
        );
    }
}
