package com.poortorich.user.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.global.response.Response;
import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.entity.enums.Role;
import com.poortorich.user.facade.UserFacade;
import com.poortorich.user.request.EmailUpdateRequest;
import com.poortorich.user.request.FindUsernameRequest;
import com.poortorich.user.request.NicknameCheckRequest;
import com.poortorich.user.request.PasswordResetRequest;
import com.poortorich.user.request.PasswordUpdateRequest;
import com.poortorich.user.request.ProfileUpdateRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.request.UsernameCheckRequest;
import com.poortorich.user.response.enums.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PutMapping("/password")
    public ResponseEntity<BaseResponse> updateUserPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest
    ) {
        Response response = userFacade.updateUserPassword(userDetails.getUsername(), passwordUpdateRequest);
        return BaseResponse.toResponseEntity(response);
    }

    @PutMapping("/email")
    public ResponseEntity<BaseResponse> updateUserEmail(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid EmailUpdateRequest emailUpdateRequest
    ) {
        Response response = userFacade.updateUserEmail(userDetails.getUsername(), emailUpdateRequest);
        return BaseResponse.toResponseEntity(response);
    }

    @DeleteMapping("/reset")
    public ResponseEntity<BaseResponse> resetUserData(@AuthenticationPrincipal UserDetails userDetails) {
        Response response = userFacade.resetAllByUser(userDetails.getUsername());
        return BaseResponse.toResponseEntity(response);
    }

    @DeleteMapping("/leave")
    public ResponseEntity<BaseResponse> deleteUserAccount(
            HttpServletResponse response,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userFacade.resetAllByUser(userDetails.getUsername());
        return BaseResponse.toResponseEntity(userFacade.deleteUserAccount(userDetails.getUsername(), response));
    }

    @GetMapping("/role")
    public ResponseEntity<BaseResponse> getUserRole(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponse.toResponseEntity(
                UserResponse.USER_ROLE_FIND_SUCCESS,
                userFacade.getUserRole(userDetails.getUsername()));
    }

    @PostMapping("/username-recovery")
    public ResponseEntity<BaseResponse> findUsername(@RequestBody @Valid FindUsernameRequest findUsernameRequest) {
        return DataResponse.toResponseEntity(
                UserResponse.FIND_USERNAME_SUCCESS,
                userFacade.findUsername(findUsernameRequest)
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse> resetPassword(@RequestBody @Valid PasswordResetRequest passwordResetRequest) {
        userFacade.resetPassword(passwordResetRequest);
        return BaseResponse.toResponseEntity(UserResponse.PASSWORD_UPDATE_SUCCESS);
    }

    @GetMapping("/oauth/profile")
    public ResponseEntity<BaseResponse> getOAuthUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return DataResponse.toResponseEntity(
                UserResponse.USER_DETAIL_FIND_SUCCESS,
                userFacade.getUserDetails(userDetails.getUsername())
        );
    }

    @PutMapping("/oauth/profile")
    public ResponseEntity<BaseResponse> updateOAuthUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid ProfileUpdateRequest profile
    ) {
        userFacade.updateUserProfile(userDetails.getUsername(), profile);
        userFacade.updateUserRole(userDetails.getUsername(), Role.USER);
        return BaseResponse.toResponseEntity(UserResponse.USER_PROFILE_UPDATE_SUCCESS);
    }
}
