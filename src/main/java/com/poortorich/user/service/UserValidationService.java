package com.poortorich.user.service;

import com.poortorich.email.response.enums.EmailResponse;
import com.poortorich.email.util.EmailVerificationPolicyManager;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.ConflictException;
import com.poortorich.global.exceptions.ForbiddenException;
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
        userValidator.validatePasswordMatch(
                userRegistrationRequest.getPassword(),
                userRegistrationRequest.getPasswordConfirm()
        );
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
        if (userReservationService.existsByUsername(username)) {
            throw new ConflictException(UserResponse.USERNAME_DUPLICATE);
        }
    }

    public void validateCheckNickname(String nickname) {
        userValidator.validateNicknameDuplicate(nickname);
        if (userReservationService.existsByNickname(nickname)) {
            throw new ConflictException(UserResponse.NICKNAME_DUPLICATE);
        }
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
        userValidator.validatePasswordCorrect(username, passwordUpdateRequest.getCurrentPassword());
        userValidator.validatePasswordMatch(
                passwordUpdateRequest.getNewPassword(),
                passwordUpdateRequest.getConfirmNewPassword()
        );
    }
}
