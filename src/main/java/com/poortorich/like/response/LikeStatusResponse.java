package com.poortorich.like.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeStatusResponse {

    private Boolean isLiked;
    private Long likeCount;
}
