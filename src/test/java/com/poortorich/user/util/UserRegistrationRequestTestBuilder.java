package com.poortorich.user.util;

import com.poortorich.s3.util.S3TestFileGenerator;
import com.poortorich.user.fixture.UserFixture;
import com.poortorich.user.request.UserRegistrationRequest;
import org.springframework.web.multipart.MultipartFile;

public class UserRegistrationRequestTestBuilder {

    private String name = UserFixture.VALID_NAME_SAMPLE_1;
    private String nickname = UserFixture.VALID_NICKNAME_SAMPLE_1;
    private String username = UserFixture.VALID_USERNAME_SAMPLE_1;
    private String password = UserFixture.VALID_PASSWORD_SAMPLE_1;
    private String passwordConfirm = UserFixture.VALID_PASSWORD_SAMPLE_1;
    private String birth = UserFixture.VALID_BIRTH_SAMPLE_1;
    private String email = UserFixture.VALID_EMAIL;
    private String gender = UserFixture.VALID_MALE;
    private String job = UserFixture.VALID_JOB_SAMPLE_1;
    private MultipartFile profileImage = S3TestFileGenerator.createJpegFile();

    public UserRegistrationRequestTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserRegistrationRequestTestBuilder nickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public UserRegistrationRequestTestBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserRegistrationRequestTestBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserRegistrationRequestTestBuilder passwordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
        return this;
    }

    public UserRegistrationRequestTestBuilder birth(String birth) {
        this.birth = birth;
        return this;
    }

    public UserRegistrationRequestTestBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserRegistrationRequestTestBuilder gender(String gender) {
        this.gender = gender;
        return this;
    }

    public UserRegistrationRequestTestBuilder job(String job) {
        this.job = job;
        return this;
    }

    public UserRegistrationRequestTestBuilder profileImage(MultipartFile profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public UserRegistrationRequest build() {
        return new UserRegistrationRequest(
                name,
                nickname,
                username,
                password,
                passwordConfirm,
                birth,
                email,
                gender,
                job,
                profileImage
        );
    }
}
