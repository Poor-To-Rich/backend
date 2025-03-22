package com.poortorich.user.validator;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.ConflictException;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.response.enums.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;
    
    public void validateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException(UserResponse.USERNAME_DUPLICATE);
        }
    }

    public void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException((UserResponse.EMAIL_DUPLICATE));
        }
    }

    public void validateNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new ConflictException(UserResponse.NICKNAME_DUPLICATE);
        }
    }

    public void validatePasswordMatch(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new BadRequestException(UserResponse.PASSWORD_NOT_MATCH);
        }
    }
}
