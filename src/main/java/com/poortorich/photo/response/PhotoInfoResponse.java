package com.poortorich.photo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoInfoResponse {

    private Long photoId;
    private String photoUrl;
}
