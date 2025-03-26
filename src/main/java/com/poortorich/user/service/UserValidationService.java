package com.poortorich.user.service;

import com.poortorich.email.enums.EmailResponse;
import com.poortorich.email.util.EmailVerificationPolicyManager;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserValidator userValidator;
    private final EmailVerificationPolicyManager emailVerificationPolicyManager;

    public void validateUserRegistrationRequest(UserRegistrationRequest userRegistrationRequest) {
        userValidator.validateUsername(userRegistrationRequest.getUsername());
        userValidator.validateNickname(userRegistrationRequest.getNickname());
        userValidator.validatePasswordMatch(
                userRegistrationRequest.getPassword(),
                userRegistrationRequest.getUserValidationConstraints()
        );
        userValidator.validateEmail(userRegistrationRequest.getEmail());

        if (emailVerificationPolicyManager.isVerifiedMail(userRegistrationRequest.getEmail())) {
            throw new BadRequestException(EmailResponse.EMAIL_NOT_VERIFIED);
        }
    }
}
