package com.poortorich.user.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.facade.UserFacade;
import com.poortorich.user.request.NicknameCheckRequest;
import com.poortorich.user.request.ProfileUpdateRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.request.UsernameCheckRequest;
import com.poortorich.user.response.enums.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserFacade userFacade;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> registerUser(
            @Valid
            @NotNull(message = UserResponseMessages.REGISTRATION_REQUEST_REQUIRED)
            UserRegistrationRequest userRegistrationRequest
    ) {
        userFacade.registerNewUser(userRegistrationRequest);
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

    @GetMapping("/detail")
    public ResponseEntity<BaseResponse> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponse.toResponseEntity(
                UserResponse.USER_DETAIL_FIND_SUCCESS,
                userFacade.getUserDetails(userDetails.getUsername())
        );
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid ProfileUpdateRequest userProfile
    ) {
        return BaseResponse.toResponseEntity(userFacade.updateUserProfile(userDetails.getUsername(), userProfile));
    }

    @GetMapping("/email")
    public ResponseEntity<BaseResponse> getUserEmail(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponse.toResponseEntity(
                UserResponse.USER_EMAIL_FIND_SUCCESS,
                userFacade.getUserEmail(userDetails.getUsername())
        );
    }
}
