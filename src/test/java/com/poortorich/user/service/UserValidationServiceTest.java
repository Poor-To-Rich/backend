package com.poortorich.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.poortorich.email.response.enums.EmailResponse;
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

    @Mock
    RedisUserReservationService userReservationService;

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

        when(emailVerificationPolicyManager.isEmailVerified(Mockito.anyString())).thenReturn(true);
        when(userReservationService.existsByUsername(request.getUsername())).thenReturn(true);
        when(userReservationService.existsByNickname(request.getNickname())).thenReturn(true);

        userValidationService.validateRegistration(request);

        verify(userValidator).validateUsernameDuplicate(request.getUsername());
        verify(userValidator).validateNicknameDuplicate(request.getNickname());
        verify(userValidator).validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        verify(userValidator).validateEmailDuplicate(request.getEmail());
        verify(userValidator).validateBirthIsInFuture(request.parseBirthday());
        verify(userReservationService).existsByUsername(request.getUsername());
        verify(userReservationService).existsByNickname(request.getNickname());
        verify(emailVerificationPolicyManager).isEmailVerified(request.getEmail());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 이메일 인증이 되지 않은 경우 예외 발생")
    void validateRegistration_whenEmailIsNotVerified_thenThrowForbiddenException() {
        UserRegistrationRequest request = userRegistrationBuilder.build();

        when(emailVerificationPolicyManager.isEmailVerified(any())).thenReturn(false);
        when(userReservationService.existsByUsername(request.getUsername())).thenReturn(true);
        when(userReservationService.existsByNickname(request.getNickname())).thenReturn(true);

        assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(EmailResponse.EMAIL_NOT_VERIFIED.getMessage());

        verify(userValidator).validateUsernameDuplicate(request.getUsername());
        verify(userValidator).validateNicknameDuplicate(request.getNickname());
        verify(userValidator).validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        verify(userValidator).validateEmailDuplicate(request.getEmail());
        verify(userValidator).validateBirthIsInFuture(request.parseBirthday());
        verify(userReservationService).existsByUsername(request.getUsername());
        verify(userReservationService).existsByNickname(request.getNickname());
        verify(emailVerificationPolicyManager).isEmailVerified(request.getEmail());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 아이디가 중복인 경우 예외 발생")
    void validateRegistration_whenUsernameDuplicate_thenThrowException() {
        UserRegistrationRequest request = userRegistrationBuilder.build();

        doThrow(new ConflictException(UserResponse.USERNAME_DUPLICATE))
                .when(userValidator)
                .validateUsernameDuplicate(request.getUsername());

        assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage(UserResponseMessages.USERNAME_DUPLICATE);

        verify(userValidator).validateUsernameDuplicate(request.getUsername());
        verify(userValidator, never()).validateNicknameDuplicate(any());
        verify(userValidator, never()).validatePasswordMatch(any(), any());
        verify(userValidator, never()).validateEmailDuplicate(any());
        verify(userValidator, never()).validateBirthIsInFuture(any());
        verify(userReservationService, never()).existsByUsername(any());
        verify(userReservationService, never()).existsByNickname(any());
        verify(emailVerificationPolicyManager, never()).isEmailVerified(any());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 닉네임이 중복인 경우 예외 발생")
    void validateRegistration_whenNicknameDuplicate_thenThrowConflictException() {
        UserRegistrationRequest request = userRegistrationBuilder.build();

        doThrow(new ConflictException(UserResponse.NICKNAME_DUPLICATE))
                .when(userValidator)
                .validateNicknameDuplicate(request.getNickname());

        assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage(UserResponseMessages.NICKNAME_DUPLICATE);

        verify(userValidator).validateUsernameDuplicate(request.getUsername());
        verify(userValidator).validateNicknameDuplicate(request.getNickname());
        verify(userValidator, never()).validatePasswordMatch(any(), any());
        verify(userValidator, never()).validateEmailDuplicate(any());
        verify(userValidator, never()).validateBirthIsInFuture(any());
        verify(userReservationService, never()).existsByUsername(any());
        verify(userReservationService, never()).existsByNickname(any());
        verify(emailVerificationPolicyManager, never()).isEmailVerified(any());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 비밀번호가 일치하지 않는 경우 예외 발생")
    void validateRegistration_whenPasswordsDoNotMatch_thenThrowBadRequestException() {
        UserRegistrationRequest request = userRegistrationBuilder
                .passwordConfirm(UserRegistrationFixture.MISMATCH_PASSWORD_CONFIRM)
                .build();

        doThrow(new BadRequestException(UserResponse.PASSWORD_DO_NOT_MATCH))
                .when(userValidator)
                .validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());

        assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserResponseMessages.PASSWORD_DO_NOT_MATCH);

        verify(userValidator).validateUsernameDuplicate(request.getUsername());
        verify(userValidator).validateNicknameDuplicate(request.getNickname());
        verify(userValidator).validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        verify(userValidator, never()).validateEmailDuplicate(any());
        verify(userValidator, never()).validateBirthIsInFuture(any());
        verify(userReservationService, never()).existsByUsername(any());
        verify(userReservationService, never()).existsByNickname(any());
        verify(emailVerificationPolicyManager, never()).isEmailVerified(any());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 이메일이 이미 존재하는 경우 예외 발생")
    void validateRegistration_whenEmailDuplicate_thenThrowConflictException() {
        UserRegistrationRequest request = userRegistrationBuilder.build();

        doThrow(new ConflictException(UserResponse.EMAIL_DUPLICATE))
                .when(userValidator)
                .validateEmailDuplicate(request.getEmail());

        assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage(UserResponseMessages.EMAIL_DUPLICATE);

        verify(userValidator).validateUsernameDuplicate(request.getUsername());
        verify(userValidator).validateNicknameDuplicate(request.getNickname());
        verify(userValidator).validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        verify(userValidator).validateEmailDuplicate(request.getEmail());
        verify(userValidator, never()).validateBirthIsInFuture(any());
        verify(userReservationService, never()).existsByUsername(any());
        verify(userReservationService, never()).existsByNickname(any());
        verify(emailVerificationPolicyManager, never()).isEmailVerified(any());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 생년월일이 미래인 경우 예외를 던진다.")
    void validateRegistration_whenBirthdayIsInFuture_thenThrowBadRequestException() {
        UserRegistrationRequest request = userRegistrationBuilder
                .email(UserRegistrationFixture.FUTURE_BIRTH)
                .build();

        doThrow(new BadRequestException(UserResponse.BIRTHDAY_IN_FUTURE))
                .when(userValidator)
                .validateBirthIsInFuture(request.parseBirthday());

        assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserResponseMessages.BIRTHDAY_IN_FUTURE);

        verify(userValidator).validateUsernameDuplicate(request.getUsername());
        verify(userValidator).validateNicknameDuplicate(request.getNickname());
        verify(userValidator).validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        verify(userValidator).validateEmailDuplicate(request.getEmail());
        verify(userValidator).validateBirthIsInFuture(request.parseBirthday());
        verify(userReservationService, never()).existsByUsername(any());
        verify(userReservationService, never()).existsByNickname(any());
        verify(emailVerificationPolicyManager, never()).isEmailVerified(any());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 사용자명 중복 검사를 수행하지 않은 경우")
    void validateRegistration_whenUsernameIsNotReserved_thenThrowBadRequestException() {
        UserRegistrationRequest request = userRegistrationBuilder.build();

        when(userReservationService.existsByUsername(any())).thenReturn(false);

        assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserResponseMessages.USERNAME_RESERVE_CHECK_REQUIRED);

        verify(userValidator).validateUsernameDuplicate(request.getUsername());
        verify(userValidator).validateNicknameDuplicate(request.getNickname());
        verify(userValidator).validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        verify(userValidator).validateEmailDuplicate(request.getEmail());
        verify(userValidator).validateBirthIsInFuture(request.parseBirthday());
        verify(userReservationService).existsByUsername(request.getUsername());
        verify(userReservationService, never()).existsByNickname(any());
        verify(emailVerificationPolicyManager, never()).isEmailVerified(any());
    }

    @Test
    @DisplayName("회원가입 데이터 검증 - 닉네임 중복 검사를 수행하지 않은 경우")
    void validateRegistration_whenNicknameIsNotReserved_thenThrowBadRequestException() {
        UserRegistrationRequest request = userRegistrationBuilder.build();

        when(userReservationService.existsByUsername(any())).thenReturn(true);
        when(userReservationService.existsByNickname(any())).thenReturn(false);

        assertThatThrownBy(() -> userValidationService.validateRegistration(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserResponseMessages.NICKNAME_RESERVE_CHECK_REQUIRED);

        verify(userValidator).validateUsernameDuplicate(request.getUsername());
        verify(userValidator).validateNicknameDuplicate(request.getNickname());
        verify(userValidator).validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        verify(userValidator).validateEmailDuplicate(request.getEmail());
        verify(userValidator).validateBirthIsInFuture(request.parseBirthday());
        verify(userReservationService).existsByUsername(request.getUsername());
        verify(userReservationService).existsByNickname(request.getNickname());
        verify(emailVerificationPolicyManager, never()).isEmailVerified(any());
    }

    @Test
    @DisplayName("사용자명 중복검사 검증 - 성공")
    void validateCheckUsername_whenAllValidationPass_thenDoNothing() {
        String username = userRegistrationBuilder.build().getUsername();

        when(userReservationService.existsByUsername(username)).thenReturn(false);

        userValidationService.validateCheckUsername(username);

        verify(userValidator).validateUsernameDuplicate(username);
        verify(userReservationService).existsByUsername(username);
    }

    @Test
    @DisplayName("사용자명 중복 검사 검증 - 사용자명으로 회원가입한 사용자가 있는 경우")
    void validateCheckUsername_whenUsernameDuplicated_thenThrowConflictException() {
        String username = userRegistrationBuilder.build().getUsername();

        doThrow(new ConflictException(UserResponse.USERNAME_DUPLICATE))
                .when(userValidator)
                .validateUsernameDuplicate(username);

        assertThatThrownBy(() -> userValidationService.validateCheckUsername(username))
                .isInstanceOf(ConflictException.class)
                .hasMessage(UserResponseMessages.USERNAME_DUPLICATE);

        verify(userValidator).validateUsernameDuplicate(username);
        verify(userReservationService, never()).existsByUsername(username);
    }

    @Test
    @DisplayName("사용자명 중복 검사 검증 - 사용자명이 이미 예약되어 있을 때")
    void validateCheckUsername_whenUsernameReserved_thenThrowConflicException() {
        String username = userRegistrationBuilder.build().getUsername();

        when(userReservationService.existsByUsername(username)).thenReturn(true);

        assertThatThrownBy(() -> userValidationService.validateCheckUsername(username))
                .isInstanceOf(ConflictException.class)
                .hasMessage(UserResponseMessages.USERNAME_DUPLICATE);

        verify(userValidator).validateUsernameDuplicate(username);
        verify(userReservationService).existsByUsername(username);
    }

    @Test
    @DisplayName("닉네임 증복 검사 검증 - 성공")
    void validateCheckNickname_whenAllValidationPass_thenDoNothing() {
        String nickname = userRegistrationBuilder.build().getNickname();

        when(userReservationService.existsByNickname(nickname)).thenReturn(false);

        userValidationService.validateCheckNickname(nickname);

        verify(userValidator).validateNicknameDuplicate(nickname);
        verify(userReservationService).existsByNickname(nickname);
    }

    @Test
    @DisplayName("닉네임 중복 검사 검증 - 이미 사용중인 닉네임인 경우")
    void validateCheckNickname_whenNicknameDuplicated_thenThrowConflicException() {
        String nickname = userRegistrationBuilder.build().getNickname();

        doThrow(new ConflictException(UserResponse.NICKNAME_DUPLICATE))
                .when(userValidator)
                .validateNicknameDuplicate(nickname);

        assertThatThrownBy(() -> userValidationService.validateCheckNickname(nickname))
                .isInstanceOf(ConflictException.class)
                .hasMessage(UserResponseMessages.NICKNAME_DUPLICATE);

        verify(userValidator).validateNicknameDuplicate(nickname);
        verify(userReservationService, never()).existsByNickname(nickname);
    }

    @Test
    @DisplayName("닉네임 중복 검사 검증 - 이미 예약중인 닉네임인 경우")
    void validateCheckNickname_whenNicknameReserved_thenThrowConflictException() {
        String nickname = userRegistrationBuilder.build().getNickname();

        when(userReservationService.existsByNickname(nickname)).thenReturn(true);

        assertThatThrownBy(() -> userValidationService.validateCheckNickname(nickname))
                .isInstanceOf(ConflictException.class)
                .hasMessage(UserResponseMessages.NICKNAME_DUPLICATE);

        verify(userValidator).validateNicknameDuplicate(nickname);
        verify(userReservationService).existsByNickname(nickname);
    }
}
