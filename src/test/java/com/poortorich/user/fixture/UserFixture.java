package com.poortorich.user.fixture;

import com.poortorich.user.entity.User;
import com.poortorich.user.entity.enums.Gender;
import java.time.LocalDate;

public class UserFixture {
    public static final String VALID_NAME = "홍길동";
    public static final String VALID_NICKNAME = "happy123";
    public static final String VALID_USERNAME = "username1";

    public static final String VALID_PASSWORD = "Pass1234!";
    public static final String MISMATCH_PASSWORD_CONFIRM = "Different123!";

    public static final String VALID_BIRTH = "2000-01-01";
    public static final String FUTURE_BIRTH = "2150-01-01";

    public static final String VALID_EMAIL = "valid@email.com";

    public static final String VALID_MALE = "MALE";
    public static final String VALID_FEMALE = "FEMALE";

    public static final String VALID_JOB = "개발자";

    public static final String TEST_PROFILE_IMAGE_URL = "http://example.com/profile.jpg";
    public static final String TEST_ENCODED_PASSWORD = "ENCODEDPASSWORD32$#$##!";

    private UserFixture() {
    }

    public static User createDefaultUser() {
        return User.builder()
                .name(VALID_NAME)
                .nickname(VALID_NICKNAME)
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .birth(LocalDate.parse(VALID_BIRTH))
                .email(VALID_EMAIL)
                .gender(Gender.from(VALID_MALE))
                .profileImage(TEST_PROFILE_IMAGE_URL)
                .build();
    }
}
