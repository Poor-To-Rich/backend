package com.poortorich.photo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String uploadedAt;
}
