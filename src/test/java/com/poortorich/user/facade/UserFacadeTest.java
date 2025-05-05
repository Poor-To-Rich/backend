package com.poortorich.user.facade;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.poortorich.s3.service.FileUploadService;
import com.poortorich.user.fixture.UserRegistrationFixture;
import com.poortorich.user.request.NicknameCheckRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.request.UsernameCheckRequest;
import com.poortorich.user.service.RedisUserReservationService;
import com.poortorich.user.service.UserService;
import com.poortorich.user.service.UserValidationService;
import com.poortorich.user.util.UserRegistrationRequestTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        String profileImageUrl = UserRegistrationFixture.TEST_PROFILE_IMAGE_URL;

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
}
