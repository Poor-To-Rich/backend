package com.poortorich.user.facade;

import com.poortorich.s3.service.FileUploadService;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.service.UserService;
import com.poortorich.user.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserValidationService userValidationService;
    private final FileUploadService fileUploadService;

    @Transactional
    public void registerNewUser(
            UserRegistrationRequest userRegistrationRequest,
            MultipartFile profileImage
    ) {
        userValidationService.validateRegistration(userRegistrationRequest);

        String profileImageUrl = fileUploadService.uploadImage(profileImage);
        userService.save(userRegistrationRequest, profileImageUrl);
    }
}
