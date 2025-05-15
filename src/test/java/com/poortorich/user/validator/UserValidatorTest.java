package com.poortorich.user.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.poortorich.auth.constants.AuthResponseMessage;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.ConflictException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.entity.User;
import com.poortorich.user.fixture.UserFixture;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.response.enums.UserResponse;
import java.time.LocalDate;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserValidator userValidator;

    @Nested
    @DisplayName("아이디 검증 테스트")
    class ValidateUsernameTest {

        @Test
        @DisplayName("사용자 이름이 이미 존재하는 경우 ConflictException을 던진다.")
        void validateUsername_whenUsernameExists_thenThrowConflictException() {
            String existingUsername = UserFixture.VALID_USERNAME_SAMPLE_1;
            when(userRepository.existsByUsername(existingUsername)).thenReturn(true);

            Assertions.assertThatThrownBy(() -> userValidator.validateUsernameDuplicate(existingUsername))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(UserResponseMessages.USERNAME_DUPLICATE);
        }

        @Test
        @DisplayName("사용자 이름이 존재하지않는 경우 예외를 발생시키지 않는다.")
        void validateUsername_whenUsernameDoesNotExists_thenNoException() {
            String newUsername = UserFixture.VALID_USERNAME_SAMPLE_1;
            when(userRepository.existsByUsername(newUsername)).thenReturn(false);

            userValidator.validateUsernameDuplicate(newUsername);
        }
    }

    @Nested
    @DisplayName("이메일 검증 테스트")
    class ValidateEmailTest {

        @Test
        @DisplayName("이메일이 이미 존재하는 경우 ConflictException을 던진다.")
        void validateEmail_whenEmailExists_thenThrowConflictException() {
            String existingEmail = UserFixture.VALID_EMAIL;
            when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

            Assertions.assertThatThrownBy(() -> userValidator.validateEmailDuplicate(existingEmail))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(UserResponseMessages.EMAIL_DUPLICATE);
        }

        @Test
        @DisplayName("이메일이 존재하지 않는 경우 예외를 던지지 않는다.")
        void validateEmail_whenEmailDoesNotExists_thenNoException() {
            String newEmail = UserFixture.VALID_EMAIL;
            when(userRepository.existsByEmail(newEmail)).thenReturn(false);

            userValidator.validateEmailDuplicate(newEmail);
        }
    }

    @Nested
    @DisplayName("닉네임 검증 테스트")
    class ValidateNicknameTest {

        @Test
        @DisplayName("닉네임이 이미 존재하는 경우 ConflictException을 던진다.")
        void validateNickname_whenNicknameExists_thenThrowConflictException() {
            String existingNickname = UserFixture.VALID_NICKNAME_SAMPLE_1;
            when(userRepository.existsByNickname(existingNickname)).thenReturn(true);

            Assertions.assertThatThrownBy(() -> userValidator.validateNicknameDuplicate(existingNickname))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(UserResponseMessages.NICKNAME_DUPLICATE);
        }

        @Test
        @DisplayName("닉네임이 존재하지 않는 경우 예외를 던지지 않는다.")
        void validateNickname_whenNicknameDoesNotExists_thenNoException() {
            String newNickname = UserFixture.VALID_NICKNAME_SAMPLE_1;
            when(userRepository.existsByNickname(newNickname)).thenReturn(false);

            userValidator.validateNicknameDuplicate(newNickname);
        }

        @Test
        @DisplayName("닉네임 변경 확인 - 닉네임이 변경 되지 않아 false를 반환")
        void isNicknameChanged_whenNicknameIsNotChanged_thenReturnFalse() {
            User user = UserFixture.createDefaultUser();

            when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

            assertThat(userValidator.isNicknameChanged(user.getUsername(), user.getNickname())).isFalse();
        }

        @Test
        @DisplayName("닉네임 변경 확인 - 닉네임이 변경되어 true를 반환")
        void isNicknameChanged_whenNicknameIsChanged_thenReturnTrue() {
            User user = UserFixture.createDefaultUser();

            when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

            assertThat(userValidator.isNicknameChanged(
                    user.getUsername(),
                    UserFixture.VALID_NICKNAME_SAMPLE_3)
            ).isTrue();
        }

        @Test
        @DisplayName("닉네임 변경 확인 - 회원을 찾을 수 없어 예외 발생")
        void isNicknameChanged_thenUserIsNotExists_thenThrowException() {

            when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userValidator.isNicknameChanged(
                    UserFixture.VALID_USERNAME_SAMPLE_1,
                    UserFixture.VALID_NICKNAME_SAMPLE_1))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(UserResponseMessages.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("비밀번호 검증 테스트")
    class ValidatePasswordTest {

        @Test
        @DisplayName("비밀번호가 일치하지 않는 경우 BadRequestException을 던진다.")
        void validatePasswordMatch_whenPasswordsDoNotMatch_thenThrowBadRequestException() {
            String password = UserFixture.VALID_PASSWORD_SAMPLE_1;
            String differentPasswordConfirm = UserFixture.MISMATCH_PASSWORD_CONFIRM;

            Assertions.assertThatThrownBy(() -> userValidator.isPasswordMatch(password, differentPasswordConfirm))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(UserResponseMessages.PASSWORD_DO_NOT_MATCH);
        }

        @Test
        @DisplayName("비밀번호가 일치하는 경우 예외를 던지지 않는다.")
        void validatePasswordMatch_whenPasswordsMatch_thenNoException() {
            String password = UserFixture.VALID_PASSWORD_SAMPLE_1;
            String passwordConfirm = UserFixture.VALID_PASSWORD_SAMPLE_1;

            userValidator.isPasswordMatch(password, passwordConfirm);
        }

        @Test
        @DisplayName("평문 비밀번호가 DB 내 인코딩된 비밀번호와 일치할 때 예외를 던지지 않는다.")
        void validatePassword_whenCurrentPasswordIsCorrect_thenNoException() {
            User mockUser = UserFixture.createDefaultUser();
            String username = UserFixture.VALID_USERNAME_SAMPLE_1;
            String currentPassword = UserFixture.VALID_PASSWORD_SAMPLE_1;

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches(currentPassword, mockUser.getPassword())).thenReturn(true);

            userValidator.validatePassword(username, currentPassword);

            verify(userRepository, times(1)).findByUsername(username);
            verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("평문 비밀번호가 DB 내 인코딩된 비밀번호와 일치하지 않아 예외를 던진다.")
        void validatePassword_whenCurrentPasswordIsIncorrect_thenThrowException() {
            User mockUser = UserFixture.createDefaultUser();
            String username = UserFixture.VALID_USERNAME_SAMPLE_1;
            String currentPassword = UserFixture.VALID_PASSWORD_SAMPLE_1;

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches(currentPassword, mockUser.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> userValidator.validatePassword(username, currentPassword))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(AuthResponseMessage.CREDENTIALS_INVALID);

            verify(userRepository, times(1)).findByUsername(username);
            verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("DB 내 유저를 찾을 수 없어 예외를 던진다.")
        void validatePassword_whenUserNotFound_thenThrowException() {
            String username = UserFixture.VALID_USERNAME_SAMPLE_1;
            String currentPassword = UserFixture.VALID_PASSWORD_SAMPLE_1;

            when(userRepository.findByUsername(username)).thenThrow(new NotFoundException(UserResponse.USER_NOT_FOUND));

            assertThatThrownBy(() -> userValidator.validatePassword(username, currentPassword))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(UserResponseMessages.USER_NOT_FOUND);

            verify(userRepository, times(1)).findByUsername(username);
            verify(passwordEncoder, never()).matches(anyString(), anyString());
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

            Assertions.assertThatThrownBy(() -> userValidator.validateBirthIsInFuture(futureBirthday))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(UserResponseMessages.BIRTHDAY_IN_FUTURE);
        }

        @Test
        @DisplayName("생년월이 과거인 경우 예외를 던지지 않는다.")
        void validateBirth_whenBirthdayIsInPast_thenNoException() {
            LocalDate pastBirthday = LocalDate.now().minusDays(ONE_DAY);

            userValidator.validateBirthIsInFuture(pastBirthday);
        }

        @Test
        @DisplayName("생년월일이 오늘인 경우 예외를 던지지 않는다.")
        void validateBirth_whenBirthdayIsToday_thenNoException() {
            LocalDate todayBirthday = LocalDate.now();

            userValidator.validateBirthIsInFuture(todayBirthday);
        }
    }
}
