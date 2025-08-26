package com.poortorich.photo.util;

import com.poortorich.photo.entity.Photo;
import com.poortorich.photo.response.AllPhotosResponse;
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

    public static AllPhotosResponse buildAllPhotosResponse(
            Boolean hasNext,
            String nextDate,
            Long nextId,
            List<Photo> photos
    ) {
        return AllPhotosResponse.builder()
                .hasNext(hasNext)
                .nextDate(nextDate)
                .nextId(nextId)
                .photos(photos.stream()
                        .map(PhotoBuilder::buildPhotoInfoByDateResponse)
                        .toList())
                .build();
    }

    private static PhotoInfoResponse buildPhotoInfoByDateResponse(Photo photo) {
        return PhotoInfoResponse.builder()
                .photoId(photo.getId())
                .photoUrl(photo.getPhotoUrl())
                .uploadedAt(photo.getCreatedDate().toLocalDate().toString())
                .build();
    }
}
