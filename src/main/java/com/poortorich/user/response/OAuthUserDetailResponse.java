package com.poortorich.user.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuthUserDetailResponse {

    private String profileImage;
    private Boolean isDefaultProfile;
    private String name;
    private String gender;
    private String job;
}
