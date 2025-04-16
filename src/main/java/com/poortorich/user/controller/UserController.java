package com.poortorich.user.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.facade.UserFacade;
import com.poortorich.user.request.NicknameCheckRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.request.UsernameCheckRequest;
import com.poortorich.user.response.enums.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserFacade userFacade;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> registerUser(
            @RequestPart("userRegistrationRequest")
            @Valid
            @NotNull(message = UserResponseMessages.REGISTRATION_REQUEST_REQUIRED)
            UserRegistrationRequest userRegistrationRequest,

            @RequestPart("profileImage")
            @NotNull(message = UserResponseMessages.PROFILE_IMAGE_REQUIRED)
            MultipartFile profileImage
    ) {
        userFacade.registerNewUser(userRegistrationRequest, profileImage);
        return BaseResponse.toResponseEntity(UserResponse.REGISTRATION_SUCCESS);
    }

    @PostMapping("/exists/username")
    public ResponseEntity<BaseResponse> checkUsername(@RequestBody @Valid UsernameCheckRequest usernameCheckRequest) {
        return BaseResponse.toResponseEntity(userFacade.checkUsernameAndReservation(usernameCheckRequest));
    }

    @PostMapping("/exists/nickname")
    public ResponseEntity<BaseResponse> checkNickname(@RequestBody @Valid NicknameCheckRequest nicknameCheckRequest) {
        return BaseResponse.toResponseEntity(userFacade.checkNicknameAndReservation(nicknameCheckRequest));
    }
}
