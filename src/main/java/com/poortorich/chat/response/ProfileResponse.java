package com.poortorich.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProfileResponse {

    private Long userId;
    private String profileImage;
    private String nickname;
    private Boolean isHost;
    private String rankingType;
}
