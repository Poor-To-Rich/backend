package com.poortorich.user.request;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.global.util.RequestValidTestHelper;
import com.poortorich.user.fixture.enums.UsernameValidationCase;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class UsernameCheckRequestTest {

    static Stream<Arguments> usernameValidationCases() {
        return TestCase.getAllTestCases(UsernameValidationCase.class);
    }

    @ParameterizedTest
    @MethodSource("usernameValidationCases")
    @DisplayName("사용자명 검증 테스트")
    void invalidUsername_shouldFailValidation(String username, String expectedErrorMessage) {
        RequestValidTestHelper.assertValidationMessage(new UsernameCheckRequest(username), expectedErrorMessage);
    }

}
