package com.poortorich.auth.request;

import com.poortorich.auth.fixture.enums.PasswordValidationCase;
import com.poortorich.auth.fixture.enums.UsernameValidationCase;
import com.poortorich.auth.util.LoginRequestTestBuilder;
import com.poortorich.global.testcases.TestCase;
import com.poortorich.global.util.RequestValidTestHelper;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LoginRequestTest {

    static Stream<Arguments> usernameValidationCases() {
        return TestCase.getAllTestCases(UsernameValidationCase.class);
    }

    static Stream<Arguments> passwordValidationCases() {
        return TestCase.getAllTestCases(PasswordValidationCase.class);
    }

    @ParameterizedTest
    @MethodSource("usernameValidationCases")
    @DisplayName("LoginRequest의 username이 유효하지 않을 때 검증에 실패한다.")
    void invalidUsername_shouldFailValidation(String username, String expectedErrorMessage) {
        LoginRequest request = LoginRequestTestBuilder.builder()
                .username(username)
                .build();

        RequestValidTestHelper.assertValidationMessage(request, expectedErrorMessage);
    }

    @ParameterizedTest
    @MethodSource("passwordValidationCases")
    @DisplayName("LoginRequest의 password가 유효하지 않을 때 검증에 실패한다.")
    void invalidPassword_shouldFailValidation(String password, String expectedErrorMessage) {
        LoginRequest request = LoginRequestTestBuilder.builder()
                .password(password)
                .build();

        RequestValidTestHelper.assertValidationMessage(request, expectedErrorMessage);
    }
    
}
