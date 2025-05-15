package com.poortorich.user.validator.enums;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.user.constants.UserResponseMessages;

public enum UsernameValidationCase implements TestCase<String, String> {
    NULL(null, UserResponseMessages.USERNAME_REQUIRED),
    EMPTY("", UserResponseMessages.USERNAME_REQUIRED),
    BLANK("user name", UserResponseMessages.USERNAME_CONTAINS_BLANK),
    TOO_LONG("VeryVeryVeryVeryVeryVeryVeryLongUSERNAME123", UserResponseMessages.USERNAME_LENGTH_INVALID),
    TOO_SHORT("us1", UserResponseMessages.USERNAME_LENGTH_INVALID),
    SPECIAL_CHAR("user123!", UserResponseMessages.USERNAME_CONTAINS_INVALID_CHAR),
    KOREAN("아이디123", UserResponseMessages.USERNAME_CONTAINS_INVALID_CHAR);

    private final String username;
    private final String expectedErrorMessage;

    UsernameValidationCase(String username, String expectedErrorMessage) {
        this.username = username;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public String getTestData() {
        return username;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
