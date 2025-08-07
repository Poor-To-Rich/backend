package com.poortorich.like.request;

import com.poortorich.like.constants.LikeResponseMessage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeUpdateRequest {

    @NotNull(message = LikeResponseMessage.IS_LIKED_REQUIRED)
    private Boolean isLiked;
}
