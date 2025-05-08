package com.poortorich.user.request;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.global.util.RequestValidTestHelper;
import com.poortorich.user.util.ProfileUpdateRequestTestBuilder;
import com.poortorich.user.validator.enums.BirthValidationCase;
import com.poortorich.user.validator.enums.NameValidationCase;
import com.poortorich.user.validator.enums.NicknameValidationCase;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ProfileUpdateRequestTest {

    static Stream<Arguments> nameValidationCases() {
        return TestCase.getAllTestCases(NameValidationCase.class);
    }

    static Stream<Arguments> nicknameValidationCases() {
        return TestCase.getAllTestCases(NicknameValidationCase.class);
    }

    static Stream<Arguments> birthValidationCases() {
        return TestCase.getAllTestCases(BirthValidationCase.class);
    }

    @ParameterizedTest
    @MethodSource("nameValidationCases")
    @DisplayName("이름 검증")
    void invalidName_ShouldFailValidation(String name, String expectedErrorMessage) {
        RequestValidTestHelper.assertValidationMessage(
                ProfileUpdateRequestTestBuilder.builder()
                        .name(name)
                        .build(),
                expectedErrorMessage
        );
    }

    @ParameterizedTest
    @MethodSource("nicknameValidationCases")
    @DisplayName("닉네임 검증")
    void invalidNickname_shouldFailValidation(String nickname, String expectedErrorMessage) {
        RequestValidTestHelper.assertValidationMessage(
                ProfileUpdateRequestTestBuilder.builder()
                        .nickname(nickname)
                        .build(),
                expectedErrorMessage
        );
    }

    @ParameterizedTest
    @MethodSource("birthValidationCases")
    @DisplayName("생일 검증")
    void invalidBirth_shouldFailValidation(String birth, String expectedErrorMessage) {
        RequestValidTestHelper.assertValidationMessage(
                ProfileUpdateRequestTestBuilder.builder()
                        .birth(birth)
                        .build(),
                expectedErrorMessage
        );
    }

}
