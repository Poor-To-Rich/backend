package com.poortorich.user.validator;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.ConflictException;
import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.fixture.UserRegistrationFixture;
import com.poortorich.user.repository.UserRepository;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Nested
    @DisplayName("아이디 검증 테스트")
    class ValidateUsernameTest {

        @Test
        @DisplayName("사용자 이름이 이미 존재하는 경우 ConflictException을 던진다.")
        void validateUsername_whenUsernameExists_thenThrowConflictException() {
            String existingUsername = UserRegistrationFixture.VALID_USERNAME;
            Mockito.when(userRepository.existsByUsername(existingUsername)).thenReturn(true);

            Assertions.assertThatThrownBy(() -> userValidator.validateUsername(existingUsername))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(UserResponseMessages.USERNAME_DUPLICATE);
        }

        @Test
        @DisplayName("사용자 이름이 존재하지않는 경우 예외를 발생시키지 않는다.")
        void validateUsername_whenUsernameDoesNotExists_thenNoException() {
            String newUsername = UserRegistrationFixture.VALID_USERNAME;
            Mockito.when(userRepository.existsByUsername(newUsername)).thenReturn(false);

            userValidator.validateUsername(newUsername);
        }
    }

    @Nested
    @DisplayName("이메일 검증 테스트")
    class ValidateEmailTest {

        @Test
        @DisplayName("이메일이 이미 존재하는 경우 ConflictException을 던진다.")
        void validateEmail_whenEmailExists_thenThrowConflictException() {
            String existingEmail = UserRegistrationFixture.VALID_EMAIL;
            Mockito.when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

            Assertions.assertThatThrownBy(() -> userValidator.validateEmail(existingEmail))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(UserResponseMessages.EMAIL_DUPLICATE);
        }

        @Test
        @DisplayName("이메일이 존재하지 않는 경우 예외를 던지지 않는다.")
        void validateEmail_whenEmailDoesNotExists_thenNoException() {
            String newEmail = UserRegistrationFixture.VALID_EMAIL;
            Mockito.when(userRepository.existsByEmail(newEmail)).thenReturn(false);

            userValidator.validateEmail(newEmail);
        }
    }

    @Nested
    @DisplayName("닉네임 검증 테스트")
    class ValidateNicknameTest {

        @Test
        @DisplayName("닉네임이 이미 존재하는 경우 ConflictException을 던진다.")
        void validateNickname_whenNicknameExists_thenThrowConflictException() {
            String existingNickname = UserRegistrationFixture.VALID_NICKNAME;
            Mockito.when(userRepository.existsByNickname(existingNickname)).thenReturn(true);

            Assertions.assertThatThrownBy(() -> userValidator.validateNickname(existingNickname))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(UserResponseMessages.NICKNAME_DUPLICATE);
        }

        @Test
        @DisplayName("닉네임이 존재하지 않는 경우 예외를 던지지 않는다.")
        void validateNickname_whenNicknameDoesNotExists_thenNoException() {
            String newNickname = UserRegistrationFixture.VALID_NICKNAME;
            Mockito.when(userRepository.existsByNickname(newNickname)).thenReturn(false);

            userValidator.validateNickname(newNickname);
        }
    }

    @Nested
    @DisplayName("비밀번호 검증 테스트")
    class ValidatePasswordTest {

        @Test
        @DisplayName("비밀번호가 일치하지 않는 경우 BadRequestException을 던진다.")
        void validatePasswordMatch_whenPasswordsDoNotMatch_thenThrowBadRequestException() {
            String password = UserRegistrationFixture.VALID_PASSWORD;
            String differentPasswordConfirm = UserRegistrationFixture.MISMATCH_PASSWORD_CONFIRM;

            Assertions.assertThatThrownBy(() -> userValidator.validatePasswordMatch(password, differentPasswordConfirm))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(UserResponseMessages.PASSWORD_DO_NOT_MATCH);
        }

        @Test
        @DisplayName("비밀번호가 일치하는 경우 예외를 던지지 않는다.")
        void validatePasswordMatch_whenPasswordsMatch_thenNoException() {
            String password = UserRegistrationFixture.VALID_PASSWORD;
            String passwordConfirm = UserRegistrationFixture.VALID_PASSWORD;

            userValidator.validatePasswordMatch(password, passwordConfirm);
        }
    }

    @Nested
    @DisplayName("생년월일 검증 테스트")
    class ValidateBrithTest {

        private static final int ONE_DAY = 1;

        @Test
        @DisplayName("생년월일이 미래인 경우 BadRequestException을 던진다.")
        void validateBirth_whenBirthdayIsInFuture_thenThrowBadRequestException() {
            LocalDate futureBirthday = LocalDate.now().plusDays(ONE_DAY);

            Assertions.assertThatThrownBy(() -> userValidator.validateBirth(futureBirthday))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(UserResponseMessages.BIRTHDAY_IN_FUTURE);
        }

        @Test
        @DisplayName("생년월이 과거인 경우 예외를 던지지 않는다.")
        void validateBirth_whenBirthdayIsInPast_thenNoException() {
            LocalDate pastBirthday = LocalDate.now().minusDays(ONE_DAY);

            userValidator.validateBirth(pastBirthday);
        }

        @Test
        @DisplayName("생년월일이 오늘인 경우 예외를 던지지 않는다.")
        void validateBirth_whenBirthdayIsToday_thenNoException() {
            LocalDate todayBirthday = LocalDate.now();

            userValidator.validateBirth(todayBirthday);
        }
    }
}
