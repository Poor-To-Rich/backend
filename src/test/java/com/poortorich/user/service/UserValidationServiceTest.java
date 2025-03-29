package com.poortorich.user.service;

import com.poortorich.email.enums.EmailResponse;
import com.poortorich.email.util.EmailVerificationPolicyManager;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.ConflictException;
import com.poortorich.global.exceptions.ForbiddenException;
import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.fixture.UserRegistrationFixture;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.response.enums.UserResponse;
import com.poortorich.user.util.UserRegistrationRequestTestBuilder;
import com.poortorich.user.validator.UserValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserValidationServiceTest {

    @Mock
    UserValidator userValidator;

    @Mock
    EmailVerificationPolicyManager emailVerificationPolicyManager;

    @InjectMocks
    UserValidationService userValidationService;

    UserRegistrationRequestTestBuilder userRegistrationBuilder;

    @BeforeEach
    void setUp() {
        userRegistrationBuilder = new UserRegistrationRequestTestBuilder();
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 모든 검증에 성공하고 이메일도 인증이 된 경우 예외를 던지지 않는다.")
    void validateRegistration_whenAllValidationsPassAndEmailIsVerified_thenNoException() {
        UserRegistrationRequest request = userRegistrationBuilder.build();

        Mockito.when(emailVerificationPolicyManager.isVerifiedMail(Mockito.anyString())).thenReturn(true);

        userValidationService.validateRegistration(request);

        Mockito.verify(userValidator).validateUsername(request.getUsername());
        Mockito.verify(userValidator).validateNickname(request.getNickname());
        Mockito.verify(userValidator).validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        Mockito.verify(userValidator).validateEmail(request.getEmail());
        Mockito.verify(userValidator).validateBirth(request.getBirth());
        Mockito.verify(emailVerificationPolicyManager).isVerifiedMail(request.getEmail());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 이메일 인증이 되지 않은 경우 예외 발생")
    void validateRegistration_whenEmailIsNotVerified_thenThrowForbiddenException() {
        UserRegistrationRequest request = userRegistrationBuilder.build();

        Mockito.when(emailVerificationPolicyManager.isVerifiedMail(Mockito.any())).thenReturn(false);

        Assertions.assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(EmailResponse.EMAIL_NOT_VERIFIED.getMessage());

        Mockito.verify(userValidator).validateUsername(request.getUsername());
        Mockito.verify(userValidator).validateNickname(request.getNickname());
        Mockito.verify(userValidator).validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        Mockito.verify(userValidator).validateEmail(request.getEmail());
        Mockito.verify(userValidator).validateBirth(request.getBirth());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 아이디가 중복인 경우 예외 발생")
    void validateRegistration_whenUsernameDuplicate_thenThrowException() {
        UserRegistrationRequest request = userRegistrationBuilder.build();

        Mockito.doThrow(new ConflictException(UserResponse.USERNAME_DUPLICATE))
                .when(userValidator)
                .validateUsername(request.getUsername());

        Assertions.assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage(UserResponseMessages.USERNAME_DUPLICATE);

        Mockito.verify(userValidator).validateUsername(request.getUsername());
        Mockito.verify(userValidator, Mockito.never()).validateNickname(Mockito.any());
        Mockito.verify(userValidator, Mockito.never()).validatePasswordMatch(Mockito.any(), Mockito.any());
        Mockito.verify(userValidator, Mockito.never()).validateEmail(Mockito.any());
        Mockito.verify(userValidator, Mockito.never()).validateBirth(Mockito.any());
        Mockito.verify(emailVerificationPolicyManager, Mockito.never()).isVerifiedMail(Mockito.any());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 닉네임이 중복인 경우 예외 발생")
    void validateRegistration_whenNicknameDuplicate_thenThrowConflictException() {
        UserRegistrationRequest request = userRegistrationBuilder.build();

        Mockito.doThrow(new ConflictException(UserResponse.NICKNAME_DUPLICATE))
                .when(userValidator)
                .validateNickname(request.getNickname());

        Assertions.assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage(UserResponseMessages.NICKNAME_DUPLICATE);

        Mockito.verify(userValidator).validateUsername(request.getUsername());
        Mockito.verify(userValidator).validateNickname(request.getNickname());
        Mockito.verify(userValidator, Mockito.never()).validatePasswordMatch(Mockito.any(), Mockito.any());
        Mockito.verify(userValidator, Mockito.never()).validateEmail(Mockito.any());
        Mockito.verify(userValidator, Mockito.never()).validateBirth(Mockito.any());
        Mockito.verify(emailVerificationPolicyManager, Mockito.never()).isVerifiedMail(Mockito.any());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 비밀번호가 일치하지 않는 경우 예외 발생")
    void validateRegistration_whenPasswordsDoNotMatch_thenThrowBadRequestException() {
        UserRegistrationRequest request = userRegistrationBuilder
                .passwordConfirm(UserRegistrationFixture.MISMATCH_PASSWORD_CONFIRM)
                .build();

        Mockito.doThrow(new BadRequestException(UserResponse.PASSWORD_NOT_MATCH))
                .when(userValidator)
                .validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());

        Assertions.assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserResponseMessages.PASSWORD_DO_NOT_MATCH);

        Mockito.verify(userValidator).validateUsername(request.getUsername());
        Mockito.verify(userValidator).validateNickname(request.getNickname());
        Mockito.verify(userValidator).validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        Mockito.verify(userValidator, Mockito.never()).validateEmail(Mockito.any());
        Mockito.verify(userValidator, Mockito.never()).validateBirth(Mockito.any());
        Mockito.verify(emailVerificationPolicyManager, Mockito.never()).isVerifiedMail(Mockito.any());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 이메일이 이미 존재하는 경우 예외 발생")
    void validateRegistration_whenEmailDuplicate_thenThrowConflictException() {
        UserRegistrationRequest request = userRegistrationBuilder.build();

        Mockito.doThrow(new ConflictException(UserResponse.EMAIL_DUPLICATE))
                .when(userValidator)
                .validateEmail(request.getEmail());

        Assertions.assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage(UserResponseMessages.EMAIL_DUPLICATE);

        Mockito.verify(userValidator).validateUsername(request.getUsername());
        Mockito.verify(userValidator).validateNickname(request.getNickname());
        Mockito.verify(userValidator).validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        Mockito.verify(userValidator).validateEmail(request.getEmail());
        Mockito.verify(userValidator, Mockito.never()).validateBirth(Mockito.any());
        Mockito.verify(emailVerificationPolicyManager, Mockito.never()).isVerifiedMail(Mockito.any());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 생년월일이 미래인 경우 예외를 던진다.")
    void validateRegistration_whenBirthdayIsInFuture_thenThrowBadRequestException() {
        UserRegistrationRequest request = userRegistrationBuilder
                .email(UserRegistrationFixture.FUTURE_BIRTH)
                .build();

        Mockito.doThrow(new BadRequestException(UserResponse.BIRTHDAY_IN_FUTURE))
                .when(userValidator)
                .validateBirth(request.getBirth());

        Assertions.assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserResponseMessages.BIRTHDAY_IN_FUTURE);

        Mockito.verify(userValidator).validateUsername(request.getUsername());
        Mockito.verify(userValidator).validateNickname(request.getNickname());
        Mockito.verify(userValidator).validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        Mockito.verify(userValidator).validateEmail(request.getEmail());
        Mockito.verify(userValidator).validateBirth(request.getBirth());
        Mockito.verify(emailVerificationPolicyManager, Mockito.never()).isVerifiedMail(Mockito.any());
    }
}
