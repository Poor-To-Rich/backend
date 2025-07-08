package com.poortorich.user.facade;

import com.poortorich.auth.jwt.util.JwtTokenManager;
import com.poortorich.category.service.CategoryService;
import com.poortorich.global.response.Response;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.user.entity.User;
import com.poortorich.user.request.EmailUpdateRequest;
import com.poortorich.user.request.NicknameCheckRequest;
import com.poortorich.user.request.PasswordUpdateRequest;
import com.poortorich.user.request.ProfileUpdateRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.request.UsernameCheckRequest;
import com.poortorich.user.response.UserDetailResponse;
import com.poortorich.user.response.UserEmailResponse;
import com.poortorich.user.response.enums.UserResponse;
import com.poortorich.user.service.RedisUserReservationService;
import com.poortorich.user.service.UserResetService;
import com.poortorich.user.service.UserService;
import com.poortorich.user.service.UserValidationService;
import jakarta.servlet.http.HttpServletResponse;
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
    private final UserResetService userResetService;
    private final CategoryService categoryService;
    private final JwtTokenManager tokenManager;

    @Transactional
    public void registerNewUser(UserRegistrationRequest userRegistrationRequest) {
        userValidationService.validateRegistration(userRegistrationRequest);
        userReservationService.removeUsernameReservation(userRegistrationRequest.getUsername());
        userReservationService.removeNicknameReservation(userRegistrationRequest.getNickname());

        String profileImageUrl = fileUploadService.uploadImage(userRegistrationRequest.getProfileImage());
        User user = userService.save(userRegistrationRequest, profileImageUrl);

        categoryService.saveAllDefaultCategories(user);
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

    public UserDetailResponse getUserDetails(String username) {
        return userService.findUserDetailByUsername(username);
    }

    @Transactional
    public Response updateUserProfile(String username, ProfileUpdateRequest userProfile) {
        userValidationService.validateUpdateUserProfile(username, userProfile);
        String newProfileImage = fileUploadService.updateImage(
                userService.findProfileImageByUsername(username),
                userProfile.getProfileImage(),
                userProfile.getIsDefaultProfile());

        userService.update(username, userProfile, newProfileImage);
        return UserResponse.USER_PROFILE_UPDATE_SUCCESS;
    }

    public UserEmailResponse getUserEmail(String username) {
        return userService.getUserEmail(username);
    }

    @Transactional
    public Response updateUserPassword(String username, PasswordUpdateRequest passwordUpdateRequest) {
        userValidationService.validateUpdateUserPassword(username, passwordUpdateRequest);
        userService.updatePassword(username, passwordUpdateRequest.getNewPassword());
        return UserResponse.PASSWORD_UPDATE_SUCCESS;
    }

    public Response resetAllByUser(String username) {
        User user = userService.findUserByUsername(username);
        userResetService.deleteUserAllData(user);
        return UserResponse.RESET_SUCCESS;
    }

    @Transactional
    public Response updateUserEmail(String username, EmailUpdateRequest emailUpdateRequest) {
        userValidationService.validateEmail(emailUpdateRequest.getEmail());
        userService.updateEmail(username, emailUpdateRequest);
        return UserResponse.USER_EMAIL_UPDATE_SUCCESS;
    }

    @Transactional
    public Response deleteUserAccount(String username, HttpServletResponse response) {
        User user = userService.findUserByUsername(username);
        tokenManager.clearAuthTokens(response);
        userResetService.deleteDefaultCategories(user);
        userService.deleteUserAccount(user);
        return UserResponse.DELETE_USER_ACCOUNT_SUCCESS;
    }
}
