package com.poortorich.user.service;

import com.poortorich.email.response.enums.EmailResponse;
import com.poortorich.email.util.EmailVerificationPolicyManager;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.ForbiddenException;
import com.poortorich.user.request.PasswordResetRequest;
import com.poortorich.user.request.PasswordUpdateRequest;
import com.poortorich.user.request.ProfileUpdateRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.response.enums.UserResponse;
import com.poortorich.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserValidator userValidator;
    private final EmailVerificationPolicyManager emailVerificationPolicyManager;
    private final RedisUserReservationService userReservationService;

    public void validateRegistration(UserRegistrationRequest userRegistrationRequest) {
        userValidator.validateUsernameDuplicate(userRegistrationRequest.getUsername());
        userValidator.validateNicknameDuplicate(userRegistrationRequest.getNickname());
        if (!userValidator.isPasswordMatch(
                userRegistrationRequest.getPassword(),
                userRegistrationRequest.getPasswordConfirm())
        ) {
            throw new BadRequestException(UserResponse.PASSWORD_DO_NOT_MATCH);
        }
        userValidator.validateEmailDuplicate(userRegistrationRequest.getEmail());
        userValidator.validateBirthIsInFuture(userRegistrationRequest.parseBirthday());

        if (!userReservationService.existsByUsername(userRegistrationRequest.getUsername())) {
            throw new BadRequestException(UserResponse.USERNAME_RESERVE_CHECK_REQUIRED);
        }

        if (!userReservationService.existsByNickname(userRegistrationRequest.getNickname())) {
            throw new BadRequestException(UserResponse.NICKNAME_RESERVE_CHECK_REQUIRED);
        }

        if (!emailVerificationPolicyManager.isEmailVerified(userRegistrationRequest.getEmail())) {
            throw new ForbiddenException(EmailResponse.EMAIL_NOT_VERIFIED);
        }
    }

    public void validateCheckUsername(String username) {
        userValidator.validateUsernameDuplicate(username);
    }

    public void validateCheckNickname(String nickname) {
        userValidator.validateNicknameDuplicate(nickname);
    }

    public void validateUpdateUserProfile(String username, ProfileUpdateRequest userProfile) {
        if (userValidator.isNicknameChanged(username, userProfile.getNickname())) {
            userValidator.validateNicknameDuplicate(userProfile.getNickname());

            if (!userReservationService.existsByNickname(userProfile.getNickname())) {
                throw new BadRequestException(UserResponse.NICKNAME_RESERVE_CHECK_REQUIRED);
            }
        }
    }

    public void validateUpdateUserPassword(String username, PasswordUpdateRequest passwordUpdateRequest) {
        userValidator.validatePassword(username, passwordUpdateRequest.getCurrentPassword());
        if (!userValidator.isPasswordMatch(
                passwordUpdateRequest.getNewPassword(),
                passwordUpdateRequest.getConfirmNewPassword())
        ) {
            throw new BadRequestException(UserResponse.NEW_PASSWORD_DO_NOT_MATCH);
        }
    }

    public void validateEmailIsVerified(String email) {
        if (!emailVerificationPolicyManager.isEmailVerified(email)) {
            throw new ForbiddenException(EmailResponse.EMAIL_NOT_VERIFIED);
        }
    }

    public void validateEmail(String email) {
        userValidator.validateEmailDuplicate(email);
        validateEmailIsVerified(email);
    }

    public void validateResetPassword(PasswordResetRequest passwordResetRequest) {
        validateEmailIsVerified(passwordResetRequest.getEmail());
        if (!userValidator.isPasswordMatch(
                passwordResetRequest.getNewPassword(),
                passwordResetRequest.getConfirmNewPassword()
        )) {
            throw new BadRequestException(UserResponse.NEW_PASSWORD_DO_NOT_MATCH);
        }
    }
}
