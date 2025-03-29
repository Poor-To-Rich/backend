package com.poortorich.user.fixture.enums;


import com.poortorich.global.TestCase;
import com.poortorich.user.constants.UserResponseMessages;

public enum NameValidationCase implements TestCase<String, String> {
    NULL(null, UserResponseMessages.NAME_REQUIRED),
    EMPTY("", UserResponseMessages.NAME_REQUIRED),
    BLANK("홍 길 동", UserResponseMessages.NAME_CONTAINS_BLANK),
    TOO_LONG("아주아주아주아주아주아주아주아주아주긴이름제한을넘기는이름", UserResponseMessages.NAME_TOO_LONG);

    private final String name;
    private final String expectedErrorMessage;

    NameValidationCase(String name, String expectedErrorMessage) {
        this.name = name;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public String getTestData() {
        return name;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
