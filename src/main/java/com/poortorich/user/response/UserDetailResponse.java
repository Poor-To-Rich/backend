package com.poortorich.user.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailResponse {
    
    private Long userId;
    private String profileImage;
    private Boolean isDefaultProfile;
    private String name;
    private String nickname;
    private String birth;
    private String gender;
    private String job;
}
