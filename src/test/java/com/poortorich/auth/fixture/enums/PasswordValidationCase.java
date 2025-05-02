package com.poortorich.auth.fixture.enums;

import com.poortorich.auth.constants.AuthResponseMessage;
import com.poortorich.global.testcases.TestCase;

public enum PasswordValidationCase implements TestCase<String, String> {

    NULL(null, AuthResponseMessage.PASSWORD_REQUIRED),
    EMPTY("", AuthResponseMessage.PASSWORD_REQUIRED);

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
