package com.poortorich.auth.util;

import com.poortorich.auth.fixture.LoginRequestFixture;
import com.poortorich.auth.request.LoginRequest;

public class LoginRequestTestBuilder {

    private String username = LoginRequestFixture.VALID_USERNAME;
    private String password = LoginRequestFixture.VALID_PASSWORD;

    public static LoginRequestTestBuilder builder() {
        return new LoginRequestTestBuilder();
    }

    public LoginRequestTestBuilder username(String username) {
        this.username = username;
        return this;
    }

    public LoginRequestTestBuilder password(String password) {
        this.password = password;
        return this;
    }

    public LoginRequest build() {
        return new LoginRequest(username, password);
    }
}
