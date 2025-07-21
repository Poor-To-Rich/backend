package com.poortorich.s3.constants;

import java.util.List;

public class S3Constants {

    public static final String DEFAULT_PROFILE_IMAGE = "https://poor-to-rich.s3.ap-northeast-2.amazonaws.com/poortorich_profileImage.png";
    public static final String KAKAO_DEFAULT_PROFILE = "http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg";

    public static final List<String> DEFAULT_PROFILES = List.of(
            DEFAULT_PROFILE_IMAGE,
            KAKAO_DEFAULT_PROFILE
    );
}
