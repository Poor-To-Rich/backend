package com.poortorich.auth.fixture.enums;

import com.poortorich.auth.constants.AuthResponseMessage;
import com.poortorich.global.testcases.TestCase;

public enum UsernameValidationCase implements TestCase<String, String> {

    NULL(null, AuthResponseMessage.USERNAME_REQUIRED),
    EMPTY("", AuthResponseMessage.USERNAME_REQUIRED);

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
