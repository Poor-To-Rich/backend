package com.poortorich.photo.util;

import com.poortorich.photo.entity.Photo;
import com.poortorich.photo.response.PhotoInfoResponse;
import com.poortorich.photo.response.PreviewPhotosResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PhotoBuilder {

    public static PreviewPhotosResponse buildPhotosResponse(List<Photo> previewPhotos) {
        return PreviewPhotosResponse.builder()
                .photos(previewPhotos.stream()
                        .filter(Objects::nonNull)
                        .map(PhotoBuilder::buildPhotoInfoResponse)
                        .toList())
                .build();
    }

    private static PhotoInfoResponse buildPhotoInfoResponse(Photo photo) {
        return PhotoInfoResponse.builder()
                .photoId(photo.getId())
                .photoUrl(photo.getPhotoUrl())
                .build();
    }
}
