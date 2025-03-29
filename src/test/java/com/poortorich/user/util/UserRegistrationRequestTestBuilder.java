package com.poortorich.user.util;

import com.poortorich.user.fixture.UserRegistrationFixture;
import com.poortorich.user.request.UserRegistrationRequest;

public class UserRegistrationRequestTestBuilder {

    private String name = UserRegistrationFixture.VALID_NAME;
    private String nickname = UserRegistrationFixture.VALID_NICKNAME;
    private String username = UserRegistrationFixture.VALID_USERNAME;
    private String password = UserRegistrationFixture.VALID_PASSWORD;
    private String passwordConfirm = UserRegistrationFixture.VALID_PASSWORD;
    private String birth = UserRegistrationFixture.VALID_BIRTH;
    private String email = UserRegistrationFixture.VALID_EMAIL;
    private String gender = UserRegistrationFixture.VALID_MALE;
    private String job = UserRegistrationFixture.VALID_JOB;

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
                job
        );
    }
}
