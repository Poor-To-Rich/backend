package com.poortorich.user.validator;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.ConflictException;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.response.enums.UserResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateUsernameDuplicate(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException(UserResponse.USERNAME_DUPLICATE, "username");
        }
    }

    public void validateEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(UserResponse.EMAIL_DUPLICATE, "email");
        }
    }

    public void validateNicknameDuplicate(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new ConflictException(UserResponse.NICKNAME_DUPLICATE, "nickname");
        }
    }

    public void validatePasswordMatch(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new BadRequestException(UserResponse.PASSWORD_DO_NOT_MATCH, "password");
        }
    }

    public void validateBirthIsInFuture(LocalDate birthday) {
        if (birthday.isAfter(LocalDate.now())) {
            throw new BadRequestException(UserResponse.BIRTHDAY_IN_FUTURE, "birth");
        }
    }
}
