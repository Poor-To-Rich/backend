package com.poortorich.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.s3.util.S3TestFileGenerator;
import com.poortorich.security.config.SecurityConfig;
import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.facade.UserFacade;
import com.poortorich.user.fixture.UserRegisterApiFixture;
import com.poortorich.user.request.NicknameCheckRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.request.UsernameCheckRequest;
import com.poortorich.user.response.enums.UserResponse;
import com.poortorich.user.util.UserRegistrationRequestTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

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
        doNothing().when(userFacade).registerNewUser(any(UserRegistrationRequest.class), any(MultipartFile.class));

        mockMvc.perform(MockMvcRequestBuilders.multipart(UserRegisterApiFixture.REGISTER_PATH)
                        .file(userRegistrationRequest)
                        .file(profileImage)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultMessage").value(UserResponse.REGISTRATION_SUCCESS.getMessage()));

        verify(userFacade, times(1)).registerNewUser(any(UserRegistrationRequest.class), any());
    }

    @Test
    @DisplayName("직업이 없는 유효한 회원가입 데이터로 registerUser api 호출 시 기대했던 응답이 반한된다.")
    public void registerUser_whenValidInputWithoutJob_thenNoException() throws Exception {
        userRegistrationRequest = UserRegisterApiFixture.createValidUserRegistrationRequestWithoutJob();

        doNothing().when(userFacade).registerNewUser(any(UserRegistrationRequest.class), any(MultipartFile.class));

        mockMvc.perform(MockMvcRequestBuilders.multipart(UserRegisterApiFixture.REGISTER_PATH)
                        .file(userRegistrationRequest)
                        .file(profileImage)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultMessage").value(UserResponse.REGISTRATION_SUCCESS.getMessage()));

        verify(userFacade, times(1)).registerNewUser(any(UserRegistrationRequest.class), any());
    }

    @Test
    @DisplayName("유효한 사용자명으로 중복 검사 api를 호출")
    public void checkUsername_whenValidUsername_thenNoException() throws Exception {
        UsernameCheckRequest request = new UsernameCheckRequest("user123");

        when(userFacade.checkUsernameAndReservation(any())).thenReturn(UserResponse.USERNAME_AVAILABLE);

        mockMvc.perform(MockMvcRequestBuilders.post("/exists/username")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultMessage").value(UserResponse.USERNAME_AVAILABLE.getMessage()));

        verify(userFacade, times(1)).checkUsernameAndReservation(any(UsernameCheckRequest.class));
    }

    @Test
    @DisplayName("유효한 닉네임으로 중복 검사 api를 호출")
    public void checkNickname_whenValidNickname_thenNoException() throws Exception {
        NicknameCheckRequest request = new NicknameCheckRequest("happy123");

        when(userFacade.checkNicknameAndReservation(any())).thenReturn(UserResponse.NICKNAME_AVAILABLE);

        mockMvc.perform(MockMvcRequestBuilders.post("/exists/nickname")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultMessage").value(UserResponseMessages.NICKNAME_AVAILABLE));

        verify(userFacade, times(1)).checkNicknameAndReservation(any(NicknameCheckRequest.class));
    }

    private String getUserRegistrationRequestJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(new UserRegistrationRequestTestBuilder().build());
    }
}
