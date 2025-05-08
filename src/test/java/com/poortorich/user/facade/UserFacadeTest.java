package com.poortorich.user.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.poortorich.global.response.Response;
import com.poortorich.s3.constants.S3TestConfig;
import com.poortorich.s3.service.FileUploadService;
import com.poortorich.user.entity.User;
import com.poortorich.user.fixture.UserFixture;
import com.poortorich.user.request.NicknameCheckRequest;
import com.poortorich.user.request.ProfileUpdateRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.request.UsernameCheckRequest;
import com.poortorich.user.response.UserDetailResponse;
import com.poortorich.user.response.enums.UserResponse;
import com.poortorich.user.service.RedisUserReservationService;
import com.poortorich.user.service.UserService;
import com.poortorich.user.service.UserValidationService;
import com.poortorich.user.util.ProfileUpdateRequestTestBuilder;
import com.poortorich.user.util.UserRegistrationRequestTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class UserFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private UserValidationService userValidationService;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private RedisUserReservationService userReservationService;

    @InjectMocks
    private UserFacade userFacade;

    private UserRegistrationRequestTestBuilder userRegistrationBuilder;

    @BeforeEach
    void setUp() {
        userRegistrationBuilder = new UserRegistrationRequestTestBuilder();
    }

    @Test
    @DisplayName("Facade에서 회원가입 시 서비스들이 적절히 호출되는지 검증")
    void registerNewUser_shouldCallServiceMethods() {
        UserRegistrationRequest request = userRegistrationBuilder.build();
        String profileImageUrl = UserFixture.TEST_PROFILE_IMAGE_URL;

        when(fileUploadService.uploadImage(request.getProfileImage())).thenReturn(profileImageUrl);

        userFacade.registerNewUser(request);

        verify(userValidationService).validateRegistration(request);
        verify(userReservationService).removeUsernameReservation(request.getUsername());
        verify(userReservationService).removeNicknameReservation(request.getNickname());
        verify(fileUploadService).uploadImage(request.getProfileImage());
        verify(userService).save(request, profileImageUrl);
    }

    @Test
    @DisplayName("Facade에서 사용자명 중복 검사 시 서비스들이 적절히 호출되는지 검증")
    void checkUsernameAndReservation_shouldCallServiceMethods() {
        UsernameCheckRequest usernameCheckRequest = new UsernameCheckRequest("user1234");

        userFacade.checkUsernameAndReservation(usernameCheckRequest);

        verify(userValidationService).validateCheckUsername(usernameCheckRequest.getUsername());
        verify(userReservationService).reservedUsername(usernameCheckRequest.getUsername());
    }

    @Test
    @DisplayName("Facade에서 닉네임 중복 검사 시 서비스들이 적절히 호출되는지 검증")
    void checkNicknameAndReservation_shouldCallServiceMethods() {
        NicknameCheckRequest nicknameCheckRequest = new NicknameCheckRequest("happy123");

        userFacade.checkNicknameAndReservation(nicknameCheckRequest);

        verify(userValidationService).validateCheckNickname(nicknameCheckRequest.getNickname());
        verify(userReservationService).reservedNickname(nicknameCheckRequest.getNickname());
    }

    @Test
    @DisplayName("Facade에서 회원 상세 조회 시 서비스들이 적절히 호출되는지 검증")
    void getUserDetails_shouldCallServiceMethods() {
        User mockUser = UserFixture.createDefaultUser();
        UserDetailResponse userDetails = UserDetailResponse.builder()
                .profileImage(mockUser.getProfileImage())
                .name(mockUser.getName())
                .nickname(mockUser.getNickname())
                .gender(mockUser.getGender().toString())
                .birth(mockUser.getBirth().toString())
                .job(mockUser.getJob())
                .build();

        when(userService.findUserDetailByUsername(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(userDetails);

        userFacade.getUserDetails(UserFixture.VALID_USERNAME_SAMPLE_1);

        verify(userService).findUserDetailByUsername(UserFixture.VALID_USERNAME_SAMPLE_1);
    }

    @Test
    @DisplayName("Facade에서 회원 프로필 편집 시 서비스들이 적절히 호출되는지 검증")
    void updateUserProfile_shouldCallServiceMethods() {
        ProfileUpdateRequest newProfile = ProfileUpdateRequestTestBuilder.builder().build();

        doNothing()
                .when(userValidationService)
                .validateUpdateUserProfile(eq(UserFixture.VALID_USERNAME_SAMPLE_1), eq(newProfile));

        when(userService.findProfileImageByUsername(eq(UserFixture.VALID_USERNAME_SAMPLE_1)))
                .thenReturn(S3TestConfig.FILE_URL_SAMPLE_1);

        when(fileUploadService.updateImage(
                anyString(),
                any(MultipartFile.class),
                anyBoolean()))
                .thenReturn(S3TestConfig.FILE_URL_SAMPLE_2);

        doNothing().when(userService).update(anyString(), any(ProfileUpdateRequest.class), anyString());

        Response result = userFacade.updateUserProfile(UserFixture.VALID_USERNAME_SAMPLE_1, newProfile);

        assertThat(result).isEqualTo(UserResponse.USER_PROFILE_UPDATE_SUCCESS);

        verify(userValidationService, times(1))
                .validateUpdateUserProfile(eq(UserFixture.VALID_USERNAME_SAMPLE_1), eq(newProfile));

        verify(userService, times(1))
                .findProfileImageByUsername(eq(UserFixture.VALID_USERNAME_SAMPLE_1));

        verify(fileUploadService, times(1))
                .updateImage(
                        eq(S3TestConfig.FILE_URL_SAMPLE_1),
                        eq(newProfile.getProfileImage()),
                        eq(newProfile.getIsDefaultProfile())
                );

        verify(userService, times(1))
                .update(
                        eq(UserFixture.VALID_USERNAME_SAMPLE_1),
                        eq(newProfile),
                        eq(S3TestConfig.FILE_URL_SAMPLE_2)
                );
    }
}
