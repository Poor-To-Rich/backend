package com.poortorich.user.fixture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.user.util.UserRegistrationRequestTestBuilder;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class UserRegisterApiFixture {
    public static final String REGISTER_PATH = "/user/register";

    public static final String USER_REGISTRATION_REQUEST = "userRegistrationRequest";
    public static final String USER_PROFILE_IMAGE_FORM_DATA = "profileImage";

    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final UserRegistrationRequestTestBuilder userRegistrationBuilder = new UserRegistrationRequestTestBuilder();

    public static MockMultipartFile createValidUserRegistrationRequest() throws JsonProcessingException {
        return new MockMultipartFile(
                USER_REGISTRATION_REQUEST,
                "userRegistration.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(userRegistrationBuilder.build()).getBytes()
        );
    }

    public static MockMultipartFile createValidUserRegistrationRequestWithoutJob() throws JsonProcessingException {
        return new MockMultipartFile(
                USER_REGISTRATION_REQUEST,
                "userRegistration.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(userRegistrationBuilder.job(null).build()).getBytes()
        );
    }
}
