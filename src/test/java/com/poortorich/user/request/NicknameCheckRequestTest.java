package com.poortorich.user.request;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.global.util.RequestValidTestHelper;
import com.poortorich.user.validator.enums.NicknameValidationCase;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class NicknameCheckRequestTest {

    static Stream<Arguments> nicknameValidationCases() {
        return TestCase.getAllTestCases(NicknameValidationCase.class);
    }

    @ParameterizedTest
    @MethodSource("nicknameValidationCases")
    @DisplayName("닉네임 검증 테스트")
    void invalidNickname_shouldFailValidation(String nickname, String expectedErrorMessage) {
        RequestValidTestHelper.assertValidationMessage(new NicknameCheckRequest(nickname), expectedErrorMessage);
    }
}
