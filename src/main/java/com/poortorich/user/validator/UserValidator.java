package com.poortorich.user.validator;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.ConflictException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.response.enums.UserResponse;
import java.time.LocalDate;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void validateUsernameDuplicate(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException(UserResponse.USERNAME_DUPLICATE);
        }
    }

    public void validateEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(UserResponse.EMAIL_DUPLICATE);
        }
    }

    public void validateNicknameDuplicate(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new ConflictException(UserResponse.NICKNAME_DUPLICATE);
        }
    }

    public boolean isPasswordMatch(String password, String passwordConfirm) {
        return password.equals(passwordConfirm);
    }

    public void validateBirthIsInFuture(LocalDate birthday) {
        if (birthday.isAfter(LocalDate.now())) {
            throw new BadRequestException(UserResponse.BIRTHDAY_IN_FUTURE);
        }
    }

    public boolean isNicknameChanged(String username, String nickname) {
        String currentNickname = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND))
                .getNickname();

        return !Objects.equals(currentNickname, nickname);
    }

    public void validatePassword(String username, String password) {
        String userPassword = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND))
                .getPassword();

        if (!passwordEncoder.matches(password, userPassword)) {
            throw new BadRequestException(UserResponse.CURRENT_PASSWORD_IS_WRONG);
        }
    }

    public void validateEmailExists(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new NotFoundException(UserResponse.USER_NOT_FOUND);
        }
    }
}
