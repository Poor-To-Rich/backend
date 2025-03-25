package com.poortorich.user.facade;

import com.poortorich.email.enums.EmailResponse;
import com.poortorich.email.util.EmailVerificationPolicyManager;
import com.poortorich.global.response.Response;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.response.enums.UserResponse;
import com.poortorich.user.service.UserService;
import com.poortorich.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserValidator userValidator;
    private final EmailVerificationPolicyManager emailVerificationPolicyManager;
    private final FileUploadService fileUploadService;

    @Transactional
    public Response register(
            UserRegistrationRequest userRegistrationRequest,
            MultipartFile profileImage
    ) {
        userValidator.validateUsername(userRegistrationRequest.getUsername());
        userValidator.validateNickname(userRegistrationRequest.getNickname());
        userValidator.validatePasswordMatch(
                userRegistrationRequest.getPassword(),
                userRegistrationRequest.getUserValidationConstraints()
        );
        userValidator.validateEmail(userRegistrationRequest.getEmail());

        if (emailVerificationPolicyManager.isVerifiedMail(userRegistrationRequest.getEmail())) {
            return EmailResponse.EMAIL_NOT_VERIFIED;
        }

        String profileImageUrl = fileUploadService.uploadImage(profileImage);
        userService.save(userRegistrationRequest, profileImageUrl);

        return UserResponse.REGISTRATION_SUCCESS;
    }
}
