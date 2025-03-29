package com.poortorich.user.request;

import com.poortorich.global.TestCase;
import com.poortorich.user.fixture.enums.BirthValidationCase;
import com.poortorich.user.fixture.enums.EmailValidationCase;
import com.poortorich.user.fixture.enums.NameValidationCase;
import com.poortorich.user.fixture.enums.NicknameValidationCase;
import com.poortorich.user.fixture.enums.PasswordValidationCase;
import com.poortorich.user.fixture.enums.UsernameValidationCase;
import com.poortorich.user.util.UserRegistrationRequestTestBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class UserRegistrationRequestTest {

    private Validator validator;
    private UserRegistrationRequestTestBuilder userRegistrationBuilder;

    static Stream<Arguments> nameValidationCases() {
        return TestCase.getAllTestCases(NameValidationCase.class);
    }

    static Stream<Arguments> nicknameValidationCases() {
        return TestCase.getAllTestCases(NicknameValidationCase.class);
    }

    public static Stream<Arguments> usernameValidationCases() {
        return TestCase.getAllTestCases(UsernameValidationCase.class);
    }

    public static Stream<Arguments> passwordValidationCases() {
        return TestCase.getAllTestCases(PasswordValidationCase.class);
    }

    public static Stream<Arguments> birthValidationCases() {
        return TestCase.getAllTestCases(BirthValidationCase.class);
    }

    public static Stream<Arguments> emailValidationCases() {
        return TestCase.getAllTestCases(EmailValidationCase.class);
    }

    @BeforeEach
    void setUp() {
        try (LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean()) {
            validatorFactory.afterPropertiesSet();
            validator = validatorFactory.getValidator();
        }
        userRegistrationBuilder = new UserRegistrationRequestTestBuilder();
    }

    @ParameterizedTest
    @MethodSource("nameValidationCases")
    void invalidName_shouldFailValidation(String name, String expectedErrorMessage) {
        UserRegistrationRequest request = userRegistrationBuilder.name(name).build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        Assertions.assertThat(violations)
                .anyMatch(v -> v.getMessage().equals(expectedErrorMessage));
    }

    @ParameterizedTest
    @MethodSource("nicknameValidationCases")
    void invalidNickname_shouldFailValidation(String nickname, String expectedErrorMessage) {
        UserRegistrationRequest request = userRegistrationBuilder.nickname(nickname).build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        Assertions.assertThat(violations)
                .anyMatch(v -> v.getMessage().equals(expectedErrorMessage));
    }

    @ParameterizedTest
    @MethodSource("usernameValidationCases")
    void invalidUsername_shouldFailValidation(String username, String expectedErrorMessage) {
        UserRegistrationRequest request = userRegistrationBuilder.username(username).build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        Assertions.assertThat(violations)
                .anyMatch(v -> v.getMessage().equals(expectedErrorMessage));
    }

    @ParameterizedTest
    @MethodSource("passwordValidationCases")
    void invalidPassword_shouldFailValidation(String password, String expectedErrorMessage) {
        UserRegistrationRequest request = userRegistrationBuilder.password(password).build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        Assertions.assertThat(violations)
                .anyMatch(v -> v.getMessage().equals(expectedErrorMessage));
    }

    @ParameterizedTest
    @MethodSource("birthValidationCases")
    void invalidBirth_shouldFailValidation(String birth, String expectedErrorMessage) {
        UserRegistrationRequest request = userRegistrationBuilder.birth(birth).build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        Assertions.assertThat(violations)
                .anyMatch(v -> v.getMessage().equals(expectedErrorMessage));
    }

    @ParameterizedTest
    @MethodSource("emailValidationCases")
    void invalidEmail_shouldFailValidation(String email, String expectedErrorMessage) {
        UserRegistrationRequest request = userRegistrationBuilder.email(email).build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        Assertions.assertThat(violations)
                .anyMatch(v -> v.getMessage().equals(expectedErrorMessage));
    }
}
