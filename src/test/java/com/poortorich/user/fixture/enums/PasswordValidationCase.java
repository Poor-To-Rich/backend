package com.poortorich.user.fixture.enums;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.user.constants.UserResponseMessages;

public enum PasswordValidationCase implements TestCase<String, String> {
    NULL(null, UserResponseMessages.PASSWORD_REQUIRED),
    EMPTY("", UserResponseMessages.PASSWORD_REQUIRED),
    BLANK("Pass 1234!", UserResponseMessages.PASSWORD_CONTAINS_BLANK),
    TOO_SHORT("Short1!", UserResponseMessages.PASSWORD_LENGTH_INVALID),
    TOO_LONG("VeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongPassword1!", UserResponseMessages.PASSWORD_LENGTH_INVALID),
    NO_UPPERCASE("password123!", UserResponseMessages.PASSWORD_INVALID),
    NO_LOWERCASE("PASSWORD123!", UserResponseMessages.PASSWORD_INVALID),
    NO_NUMBER("Password!", UserResponseMessages.PASSWORD_INVALID),
    NO_SPECIAL_CHAR("Password123", UserResponseMessages.PASSWORD_INVALID),
    KOREAN("한글123!", UserResponseMessages.PASSWORD_CONTAINS_KOREAN);

    private final String password;
    private final String expectedErrorMessage;

    PasswordValidationCase(String password, String expectedErrorMessage) {
        this.password = password;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public String getTestData() {
        return password;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
