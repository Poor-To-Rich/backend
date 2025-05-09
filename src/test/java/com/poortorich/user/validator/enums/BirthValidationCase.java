package com.poortorich.user.validator.enums;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.user.constants.UserResponseMessages;

public enum BirthValidationCase implements TestCase<String, String> {
    NULL(null, UserResponseMessages.BIRTHDAY_REQUIRED),
    EMPTY("", UserResponseMessages.BIRTHDAY_REQUIRED),
    INVALID_FORMAT("1990/01/01", UserResponseMessages.BIRTHDAY_FORMAT_INVALID);

    private final String birth;
    private final String expectedErrorMessage;

    BirthValidationCase(String birth, String expectedErrorMessage) {
        this.birth = birth;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public String getTestData() {
        return birth;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
