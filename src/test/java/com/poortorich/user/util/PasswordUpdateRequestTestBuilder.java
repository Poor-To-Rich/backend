package com.poortorich.user.util;

import com.poortorich.user.fixture.UserFixture;
import com.poortorich.user.request.PasswordUpdateRequest;

public class PasswordUpdateRequestTestBuilder {

    private String currentPassword = UserFixture.VALID_PASSWORD_SAMPLE_1;
    private String newPassword = UserFixture.VALID_PASSWORD_SAMPLE_2;
    private String confirmNewPassword = UserFixture.VALID_PASSWORD_SAMPLE_2;

    public static PasswordUpdateRequestTestBuilder builder() {
        return new PasswordUpdateRequestTestBuilder();
    }

    public PasswordUpdateRequestTestBuilder currentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
        return this;
    }

    public PasswordUpdateRequestTestBuilder newPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    public PasswordUpdateRequestTestBuilder confirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
        return this;
    }

    public PasswordUpdateRequest build() {
        return new PasswordUpdateRequest(currentPassword, newPassword, confirmNewPassword);
    }
}
