package com.poortorich.user.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.user.constants.UserControllerConstants;
import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.facade.UserFacade;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.response.enums.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;

    @PostMapping(UserControllerConstants.REGISTER_PATH)
    public ResponseEntity<BaseResponse> registerUser(
            @RequestPart(UserControllerConstants.USER_REGISTRATION_REQUEST)
            @Valid
            @NotNull(message = UserResponseMessages.REGISTRATION_REQUEST_REQUIRED)
            UserRegistrationRequest userRegistrationRequest,

            @RequestPart(UserControllerConstants.PROFILE_IMAGE_FORM_DATA)
            @NotNull(message = UserResponseMessages.PROFILE_IMAGE_REQUIRED)
            MultipartFile profileImage
    ) {
        userFacade.registerNewUser(userRegistrationRequest, profileImage);
        return BaseResponse.toResponseEntity(UserResponse.REGISTRATION_SUCCESS);
    }
}
