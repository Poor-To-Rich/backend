package com.poortorich.photo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllPhotosResponse {

    private Boolean hasNext;
    private PhotoCursorResponse nextCursor;
    private Long photoCount;
    private List<PhotoInfoResponse> photos;
}
