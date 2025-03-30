package com.poortorich.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.s3.util.S3TestFileGenerator;
import com.poortorich.security.config.SecurityConfig;
import com.poortorich.user.constants.UserControllerConstants;
import com.poortorich.user.facade.UserFacade;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.response.enums.UserResponse;
import com.poortorich.user.util.UserRegistrationRequestTestBuilder;
import org.junit.jupiter.api.BeforeEach;
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
        userRegistrationRequest = new MockMultipartFile(
                UserControllerConstants.USER_REGISTRATION_REQUEST,
                "userRegistrationRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                getUserRegistrationRequestJson().getBytes()
        );
        profileImage = S3TestFileGenerator.createJpegFile();
    }

    @Test
    public void register_whenValidInput_thenNoException() throws Exception {
        doNothing().when(userFacade).registerNewUser(any(UserRegistrationRequest.class), any(MultipartFile.class));

        mockMvc.perform(MockMvcRequestBuilders.multipart(UserControllerConstants.REGISTER_PATH)
                        .file(userRegistrationRequest)
                        .file(profileImage)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultMessage").value(UserResponse.REGISTRATION_SUCCESS.getMessage()));

        // verify userFacade.registerNewUser was called
        verify(userFacade, times(1)).registerNewUser(any(UserRegistrationRequest.class), any());
    }

    private String getUserRegistrationRequestJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(new UserRegistrationRequestTestBuilder().build());
    }
}
