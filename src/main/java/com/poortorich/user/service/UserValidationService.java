package com.poortorich.user.service;

import com.poortorich.email.enums.EmailResponse;
import com.poortorich.email.util.EmailVerificationPolicyManager;
import com.poortorich.global.exceptions.ForbiddenException;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserValidator userValidator;
    private final EmailVerificationPolicyManager emailVerificationPolicyManager;

    public void validateRegistration(UserRegistrationRequest userRegistrationRequest) {
        userValidator.validateUsername(userRegistrationRequest.getUsername());
        userValidator.validateNickname(userRegistrationRequest.getNickname());
        userValidator.validatePasswordMatch(
                userRegistrationRequest.getPassword(),
                userRegistrationRequest.getPasswordConfirm()
        );
        userValidator.validateEmail(userRegistrationRequest.getEmail());
        userValidator.validateBirth(userRegistrationRequest.getBirth());

        if (!emailVerificationPolicyManager.isVerifiedMail(userRegistrationRequest.getEmail())) {
            throw new ForbiddenException(EmailResponse.EMAIL_NOT_VERIFIED);
        }
    }
}
