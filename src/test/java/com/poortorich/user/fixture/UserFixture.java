package com.poortorich.user.fixture;

import com.poortorich.user.entity.User;
import com.poortorich.user.entity.enums.Gender;
import java.time.LocalDate;

public class UserFixture {
    public static final String VALID_NAME_SAMPLE_1 = "홍길동";
    public static final String VALID_NAME_SAMPLE_2 = "김춘식";
    public static final String VALID_NAME_SAMPLE_3 = "신짱구";

    public static final String VALID_NICKNAME_SAMPLE_1 = "happy123";
    public static final String VALID_NICKNAME_SAMPLE_2 = "hello23";
    public static final String VALID_NICKNAME_SAMPLE_3 = "playboy2";

    public static final String VALID_USERNAME_SAMPLE_1 = "username1";
    public static final String VALID_USERNAME_SAMPLE_2 = "testuser12";
    public static final String VALID_USERNAME_SAMPLE_3 = "lee23424";

    public static final String VALID_PASSWORD_SAMPLE_1 = "Pass1234!";
    public static final String VALID_PASSWORD_SAMPLE_2 = "Qwer1234!";
    public static final String VALID_PASSWORD_SAMPLE_3 = "Asdf1234!";

    public static final String MISMATCH_PASSWORD_CONFIRM = "Different123!";

    public static final String VALID_BIRTH_SAMPLE_1 = "2000-01-02";
    public static final String VALID_BIRTH_SAMPLE_2 = "2000-01-29";
    public static final String VALID_BIRTH_SAMPLE_3 = "2000-04-11";

    public static final String FUTURE_BIRTH = "2150-01-01";

    public static final String VALID_EMAIL = "valid@email.com";

    public static final String VALID_MALE = "MALE";
    public static final String VALID_FEMALE = "FEMALE";

    public static final String VALID_JOB_SAMPLE_1 = "개발자";
    public static final String VALID_JOB_SAMPLE_2 = "UI/UX 디자이너";
    public static final String VALID_JOB_SAMPLE_3 = "백수";

    public static final String TEST_PROFILE_IMAGE_URL = "http://example.com/profile.jpg";
    public static final String TEST_ENCODED_PASSWORD = "ENCODEDPASSWORD32$#$##!";

    private UserFixture() {
    }

    public static User createDefaultUser() {
        return User.builder()
                .name(VALID_NAME_SAMPLE_1)
                .nickname(VALID_NICKNAME_SAMPLE_1)
                .username(VALID_USERNAME_SAMPLE_1)
                .password(VALID_PASSWORD_SAMPLE_1)
                .birth(LocalDate.parse(VALID_BIRTH_SAMPLE_1))
                .email(VALID_EMAIL)
                .gender(Gender.from(VALID_MALE))
                .profileImage(TEST_PROFILE_IMAGE_URL)
                .build();
    }
}
