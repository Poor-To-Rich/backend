package com.poortorich.user.facade;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.poortorich.s3.service.FileUploadService;
import com.poortorich.s3.util.S3TestFileGenerator;
import com.poortorich.user.fixture.UserRegistrationFixture;
import com.poortorich.user.request.UserRegistrationRequest;
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
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class UserFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private UserValidationService userValidationService;

    @Mock
    private FileUploadService fileUploadService;

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
        MultipartFile profileImage = S3TestFileGenerator.createJpegFile();
        String profileImageUrl = UserRegistrationFixture.TEST_PROFILE_IMAGE_URL;

        when(fileUploadService.uploadImage(profileImage)).thenReturn(profileImageUrl);

        userFacade.registerNewUser(request, profileImage);

        verify(userValidationService).validateRegistration(request);
        verify(fileUploadService).uploadImage(profileImage);
        verify(userService).save(request, profileImageUrl);
    }
}
