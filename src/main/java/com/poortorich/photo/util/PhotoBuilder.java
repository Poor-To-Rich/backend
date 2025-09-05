package com.poortorich.photo.util;

import com.poortorich.photo.entity.Photo;
import com.poortorich.photo.response.AllPhotosResponse;
import com.poortorich.photo.response.PhotoCursorResponse;
import com.poortorich.photo.response.PhotoDetailsResponse;
import com.poortorich.photo.response.PhotoInfoResponse;
import com.poortorich.photo.response.PreviewPhotosResponse;
import com.poortorich.photo.response.UploadedUserResponse;
import com.poortorich.user.entity.User;
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
                .nextCursor(buildPhotoCursorResponse(nextDate, nextId))
                .photoCount((long) photos.size())
                .photos(photos.stream()
                        .filter(Objects::nonNull)
                        .map(PhotoBuilder::buildPhotoInfoByDateResponse)
                        .toList())
                .build();
    }

    private static PhotoCursorResponse buildPhotoCursorResponse(String nextDate, Long nextId) {
        return PhotoCursorResponse.builder()
                .date(nextDate)
                .id(nextId)
                .build();
    }

    private static PhotoInfoResponse buildPhotoInfoByDateResponse(Photo photo) {
        return PhotoInfoResponse.builder()
                .photoId(photo.getId())
                .photoUrl(photo.getPhotoUrl())
                .uploadedAt(photo.getCreatedDate().toLocalDate().toString())
                .build();
    }

    public static PhotoDetailsResponse buildPhotoDetailsResponse(Photo photo, Long prevPhotoId, Long nextPhotoId) {
        return PhotoDetailsResponse.builder()
                .photoId(photo.getId())
                .photoUrl(photo.getPhotoUrl())
                .uploadedAt(photo.getCreatedDate().toString())
                .uploadedBy(buildUploadedUserResponse(photo.getUser()))
                .prevPhotoId(prevPhotoId)
                .nextPhotoId(nextPhotoId)
                .build();
    }

    private static UploadedUserResponse buildUploadedUserResponse(User user) {
        return UploadedUserResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }
}
