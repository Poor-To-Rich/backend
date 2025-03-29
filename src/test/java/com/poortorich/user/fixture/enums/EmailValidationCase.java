package com.poortorich.user.fixture.enums;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.user.constants.UserResponseMessages;

public enum EmailValidationCase implements TestCase<String, String> {
    NULL(null, UserResponseMessages.EMAIL_REQUIRED),
    EMPTY("", UserResponseMessages.EMAIL_REQUIRED),
    INVALID_FORMAT("invalid@", UserResponseMessages.EMAIL_INVALID);

    private final String email;
    private final String expectedErrorMessage;

    EmailValidationCase(String email, String expectedErrorMessage) {
        this.email = email;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public String getTestData() {
        return email;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
