package com.poortorich.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.global.config.BaseSecurityTest;
import com.poortorich.s3.util.S3TestFileGenerator;
import com.poortorich.security.config.SecurityConfig;
import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.entity.User;
import com.poortorich.user.facade.UserFacade;
import com.poortorich.user.fixture.UserFixture;
import com.poortorich.user.fixture.UserRegisterApiFixture;
import com.poortorich.user.request.NicknameCheckRequest;
import com.poortorich.user.request.PasswordUpdateRequest;
import com.poortorich.user.request.ProfileUpdateRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.request.UsernameCheckRequest;
import com.poortorich.user.response.UserDetailResponse;
import com.poortorich.user.response.UserEmailResponse;
import com.poortorich.user.response.enums.UserResponse;
import com.poortorich.user.util.PasswordUpdateRequestTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserFacade userFacade;

    private ObjectMapper objectMapper;
    private MockMultipartFile userRegistrationRequest;
    private MockMultipartFile profileImage;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        userRegistrationRequest = UserRegisterApiFixture.createValidUserRegistrationRequest();
        profileImage = S3TestFileGenerator.createJpegFile();
    }

    @Test
    @DisplayName("유효한 회원 가입 데이터로 registerUser 호출 시 기대했던 응답이 반환된다.")
    public void registerUser_whenValidInput_thenNoException() throws Exception {
        doNothing().when(userFacade).registerNewUser(any(UserRegistrationRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.multipart(UserRegisterApiFixture.REGISTER_PATH)
                        .file(profileImage)
                        .param("name", UserFixture.VALID_NAME_SAMPLE_1)
                        .param("nickname", UserFixture.VALID_NICKNAME_SAMPLE_1)
                        .param("username", UserFixture.VALID_USERNAME_SAMPLE_1)
                        .param("password", UserFixture.VALID_PASSWORD_SAMPLE_1)
                        .param("passwordConfirm", UserFixture.VALID_PASSWORD_SAMPLE_1)
                        .param("birth", UserFixture.VALID_BIRTH_SAMPLE_1)
                        .param("email", UserFixture.VALID_EMAIL)
                        .param("gender", UserFixture.VALID_MALE)
                        .param("job", UserFixture.VALID_JOB_SAMPLE_1)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(UserResponse.REGISTRATION_SUCCESS.getMessage()));

        verify(userFacade, times(1)).registerNewUser(any(UserRegistrationRequest.class));
    }

    @Test
    @DisplayName("직업이 없는 유효한 회원가입 데이터로 registerUser api 호출 시 기대했던 응답이 반한된다.")
    public void registerUser_whenValidInputWithoutJob_thenNoException() throws Exception {
        userRegistrationRequest = UserRegisterApiFixture.createValidUserRegistrationRequestWithoutJob();

        doNothing().when(userFacade).registerNewUser(any(UserRegistrationRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.multipart(UserRegisterApiFixture.REGISTER_PATH)
                        .file(profileImage)
                        .param("name", UserFixture.VALID_NAME_SAMPLE_1)
                        .param("nickname", UserFixture.VALID_NICKNAME_SAMPLE_1)
                        .param("username", UserFixture.VALID_USERNAME_SAMPLE_1)
                        .param("password", UserFixture.VALID_PASSWORD_SAMPLE_1)
                        .param("passwordConfirm", UserFixture.VALID_PASSWORD_SAMPLE_1)
                        .param("birth", UserFixture.VALID_BIRTH_SAMPLE_1)
                        .param("email", UserFixture.VALID_EMAIL)
                        .param("gender", UserFixture.VALID_MALE)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(UserResponse.REGISTRATION_SUCCESS.getMessage()));

        verify(userFacade, times(1)).registerNewUser(any(UserRegistrationRequest.class));
    }

    @Test
    @DisplayName("유효한 사용자명으로 중복 검사 api를 호출")
    public void checkUsername_whenValidUsername_thenNoException() throws Exception {
        UsernameCheckRequest request = new UsernameCheckRequest("user123");

        when(userFacade.checkUsernameAndReservation(any())).thenReturn(UserResponse.USERNAME_AVAILABLE);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/exists/username")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(UserResponse.USERNAME_AVAILABLE.getMessage()));

        verify(userFacade, times(1)).checkUsernameAndReservation(any(UsernameCheckRequest.class));
    }

    @Test
    @DisplayName("유효한 닉네임으로 중복 검사 api를 호출")
    public void checkNickname_whenValidNickname_thenNoException() throws Exception {
        NicknameCheckRequest request = new NicknameCheckRequest("happy123");

        when(userFacade.checkNicknameAndReservation(any())).thenReturn(UserResponse.NICKNAME_AVAILABLE);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/exists/nickname")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(UserResponseMessages.NICKNAME_AVAILABLE));

        verify(userFacade, times(1)).checkNicknameAndReservation(any(NicknameCheckRequest.class));
    }

    @Test
    @DisplayName("유효한 액세스 토큰으로 회원 상세 조회 api를 호출")
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    public void getUserDetails_whenValidAccessToken_thenNoException() throws Exception {
        User mockUser = UserFixture.createDefaultUser();
        UserDetailResponse response = UserDetailResponse.builder()
                .profileImage(mockUser.getProfileImage())
                .name(mockUser.getName())
                .nickname(mockUser.getNickname())
                .birth(mockUser.getBirth().toString())
                .gender(mockUser.getGender().toString())
                .job(mockUser.getJob())
                .build();

        when(userFacade.getUserDetails(UserFixture.VALID_USERNAME_SAMPLE_1)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/detail")
                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value(UserResponse.USER_DETAIL_FIND_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.profileImage").value(response.getProfileImage()))
                .andExpect(jsonPath("$.data.name").value(response.getName()))
                .andExpect(jsonPath("$.data.nickname").value(response.getNickname()))
                .andExpect(jsonPath("$.data.birth").value(response.getBirth()))
                .andExpect(jsonPath("$.data.gender").value(response.getGender()))
                .andExpect(jsonPath("$.data.job").value(response.getJob()));

        verify(userFacade).getUserDetails(UserFixture.VALID_USERNAME_SAMPLE_1);
    }

    @Test
    @DisplayName("유효한 데이터로 프로필 변경 api 호출 - 프로필 변경 X")
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    void updateUserProfile_whenValidInputWithoutProfileImage_thenNoException() throws Exception {
        when(userFacade.updateUserProfile(anyString(), any(ProfileUpdateRequest.class)))
                .thenReturn(UserResponse.USER_PROFILE_UPDATE_SUCCESS);

        mockMvc.perform(MockMvcRequestBuilders.put("/user/update")
                        .param("name", UserFixture.VALID_NAME_SAMPLE_1)
                        .param("nickname", UserFixture.VALID_NICKNAME_SAMPLE_1)
                        .param("birth", UserFixture.VALID_BIRTH_SAMPLE_1)
                        .param("gender", UserFixture.VALID_MALE)
                        .param("job", UserFixture.VALID_JOB_SAMPLE_1)
                        .with(SecurityMockMvcRequestPostProcessors.user(UserFixture.createDefaultUser()))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value(UserResponseMessages.USER_PROFILE_UPDATE_SUCCESS));

        verify(userFacade).updateUserProfile(anyString(), any(ProfileUpdateRequest.class));
    }

    @Test
    @DisplayName("유효한 데이터로 프로필 변경 api 호출 - 기본 프로필로 변경")
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    void updateUserProfile_whenValidInputWithIsDefaultImage_thenNoException() throws Exception {
        when(userFacade.updateUserProfile(anyString(), any(ProfileUpdateRequest.class)))
                .thenReturn(UserResponse.USER_PROFILE_UPDATE_SUCCESS);

        mockMvc.perform(MockMvcRequestBuilders.put("/user/update")
                        .param("isDefaultImage", "true")
                        .param("name", UserFixture.VALID_NAME_SAMPLE_1)
                        .param("nickname", UserFixture.VALID_NICKNAME_SAMPLE_1)
                        .param("birth", UserFixture.VALID_BIRTH_SAMPLE_1)
                        .param("gender", UserFixture.VALID_MALE)
                        .param("job", UserFixture.VALID_JOB_SAMPLE_1)
                        .with(SecurityMockMvcRequestPostProcessors.user(UserFixture.createDefaultUser()))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value(UserResponseMessages.USER_PROFILE_UPDATE_SUCCESS));

        verify(userFacade).updateUserProfile(anyString(), any(ProfileUpdateRequest.class));
    }

    @Test
    @DisplayName("유효한 데이터로 프로필 변경 api 호출 - 다른 프로필로 변경")
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    void updateUserProfile_whenValidInputWithProfileImage_thenNoException() throws Exception {
        when(userFacade.updateUserProfile(anyString(), any(ProfileUpdateRequest.class)))
                .thenReturn(UserResponse.USER_PROFILE_UPDATE_SUCCESS);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/user/update")
                        .file(profileImage)
                        .param("name", UserFixture.VALID_NAME_SAMPLE_1)
                        .param("nickname", UserFixture.VALID_NICKNAME_SAMPLE_1)
                        .param("birth", UserFixture.VALID_BIRTH_SAMPLE_1)
                        .param("gender", UserFixture.VALID_MALE)
                        .param("job", UserFixture.VALID_JOB_SAMPLE_1)
                        .with(SecurityMockMvcRequestPostProcessors.user(UserFixture.createDefaultUser()))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value(UserResponseMessages.USER_PROFILE_UPDATE_SUCCESS));

        verify(userFacade).updateUserProfile(anyString(), any(ProfileUpdateRequest.class));
    }

    @Test
    @DisplayName("유효한 데이터로 이메일 조회 api 호출 - 성공")
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    void getUserEmail_whenValidInput_thenNoException() throws Exception {
        User mockUser = UserFixture.createDefaultUser();
        UserEmailResponse userEmailResponse = UserEmailResponse.builder()
                .email(mockUser.getEmail())
                .build();

        when(userFacade.getUserEmail(anyString())).thenReturn(userEmailResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/email")
                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value(UserResponseMessages.USER_EMAIL_FIND_SUCCESS))
                .andExpect(jsonPath("$.data.email").value(mockUser.getEmail()));

        verify(userFacade, times(1)).getUserEmail(anyString());
    }

    @Test
    @DisplayName("유효한 데이터로 비밀번호 변경 api 호출 - 성공")
    @WithMockUser(username = UserFixture.VALID_USERNAME_SAMPLE_1)
    void updateUserPassword_whenValidInput_thenNoException() throws Exception {
        User mockUser = UserFixture.createDefaultUser();
        PasswordUpdateRequest request = PasswordUpdateRequestTestBuilder.builder().build();

        when(userFacade.updateUserPassword(anyString(), any(PasswordUpdateRequest.class)))
                .thenReturn(UserResponse.PASSWORD_UPDATE_SUCCESS);

        mockMvc.perform(MockMvcRequestBuilders.put("/user/password")
                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(getRequestJson(request)))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value(UserResponseMessages.PASSWORD_UPDATE_SUCCESS));

        verify(userFacade).updateUserPassword(anyString(), any(PasswordUpdateRequest.class));
    }

    private String getRequestJson(Object request) throws JsonProcessingException {
        return objectMapper.writeValueAsString(request);
    }
}
