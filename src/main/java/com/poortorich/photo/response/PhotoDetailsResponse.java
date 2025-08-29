package com.poortorich.photo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDetailsResponse {

    private Long photoId;
    private String photoUrl;
    private String uploadedAt;
    private UploadedUserResponse uploadedBy;
    private Long prevPhotoId;
    private Long nextPhotoId;
}
