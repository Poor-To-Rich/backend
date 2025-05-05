package com.poortorich.user.facade;

import com.poortorich.global.response.Response;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.user.request.NicknameCheckRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.request.UsernameCheckRequest;
import com.poortorich.user.response.enums.UserResponse;
import com.poortorich.user.service.RedisUserReservationService;
import com.poortorich.user.service.UserService;
import com.poortorich.user.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserValidationService userValidationService;
    private final FileUploadService fileUploadService;
    private final RedisUserReservationService userReservationService;

    @Transactional
    public void registerNewUser(UserRegistrationRequest userRegistrationRequest) {
        userValidationService.validateRegistration(userRegistrationRequest);
        userReservationService.removeUsernameReservation(userRegistrationRequest.getUsername());
        userReservationService.removeNicknameReservation(userRegistrationRequest.getNickname());

        String profileImageUrl = fileUploadService.uploadImage(userRegistrationRequest.getProfileImage());
        userService.save(userRegistrationRequest, profileImageUrl);
    }

    public Response checkUsernameAndReservation(UsernameCheckRequest usernameCheckRequest) {
        userValidationService.validateCheckUsername(usernameCheckRequest.getUsername());
        userReservationService.reservedUsername(usernameCheckRequest.getUsername());
        return UserResponse.USERNAME_AVAILABLE;
    }

    public Response checkNicknameAndReservation(NicknameCheckRequest nicknameCheckRequest) {
        userValidationService.validateCheckNickname(nicknameCheckRequest.getNickname());
        userReservationService.reservedNickname(nicknameCheckRequest.getNickname());
        return UserResponse.NICKNAME_AVAILABLE;
    }
}
