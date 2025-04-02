package com.poortorich.user.fixture.enums;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.user.constants.UserResponseMessages;

public enum NicknameValidationCase implements TestCase<String, String> {
    NULL(null, UserResponseMessages.NICKNAME_REQUIRED),
    EMPTY("", UserResponseMessages.NICKNAME_REQUIRED),
    BLANK("happy 123", UserResponseMessages.NICKNAME_CONTAINS_BLANK),
    TOO_LONG("아주아주아주아주아주아주아주아주아주아주긴닉네임길이제한을넘기는닉네임", UserResponseMessages.NICKNAME_TOO_LONG),
    INVALID_CHAR("4nickname", UserResponseMessages.NICKNAME_INVALID_START_CHAR),
    SPECIAL_CHAR("nickn@ame", UserResponseMessages.NICKNAME_CONTAINS_SPECIAL_CHAR);

    private final String nickname;
    private final String expectedErrorMessage;

    NicknameValidationCase(String nickname, String expectedErrorMessage) {
        this.nickname = nickname;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public String getTestData() {
        return nickname;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
