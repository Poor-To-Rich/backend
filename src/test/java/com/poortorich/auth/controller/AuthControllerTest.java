package com.poortorich.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.auth.constants.AuthResponseMessage;
import com.poortorich.auth.request.LoginRequest;
import com.poortorich.auth.response.enums.AuthResponse;
import com.poortorich.auth.service.AuthService;
import com.poortorich.auth.util.LoginRequestTestBuilder;
import com.poortorich.global.config.BaseSecurityTest;
import com.poortorich.global.config.UtilObjectConfig;
import com.poortorich.security.config.SecurityConfig;
import com.poortorich.user.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, UtilObjectConfig.class})
public class AuthControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        validLoginRequest = LoginRequestTestBuilder.builder().build();
    }

    @Test
    @DisplayName("로그인 요청 테스트 성공")
    void login_ShouldReturnLoginSuccessResponse_WhenLoginRequestIsValid() throws Exception {
        ResultActions actions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest))
                .with(csrf()));

        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(AuthResponse.LOGIN_SUCCESS.getHttpStatus().value()))
                .andExpect(jsonPath("$.message").value(AuthResponse.LOGIN_SUCCESS.getMessage()));

        verify(authService, Mockito.times(1)).login(any(LoginRequest.class), any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("로그인 - 사용자명이 null일 때 사용자명을 입력하라는 응답을 반환")
    void login_ShouldUsernameRequiredMessage_WhenUsernameIsNull() throws Exception {
        LoginRequest loginRequestWithNullUsername = LoginRequestTestBuilder.builder().username(null).build();

        ResultActions actions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestWithNullUsername))
                .with(csrf()));

        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(AuthResponseMessage.USERNAME_REQUIRED));

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("로그인 - 비밀번호가 null일 때 비밀번호를 입력하라는 응답을 반환함")
    void login_ShouldReturnPasswordRequiredMessage_WhenPasswordIsNull() throws Exception {
        LoginRequest loginRequestWithNullPassword = LoginRequestTestBuilder.builder().password(null).build();

        ResultActions actions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestWithNullPassword))
                .with(csrf()));

        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(AuthResponseMessage.PASSWORD_REQUIRED));

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("토큰 갱신 - 리프레시 토큰이 유효할 때 토큰 갱신 성공 응답을 반환함")
    void refreshToken_ShouldReturnTokenRefreshSuccessResponse_WhenRefreshTokenIsValid() throws Exception {
        ResultActions actions = mockMvc.perform(post("/auth/refresh"));

        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(AuthResponse.TOKEN_REFRESH_SUCCESS.getHttpStatus().value()))
                .andExpect(jsonPath("$.message").value(AuthResponse.TOKEN_REFRESH_SUCCESS.getMessage()));

        verify(authService, times(1)).refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("토큰 갱신 - 리프레시 토큰이 유효할 때 토큰 갱신 성공 응답을 반환함")
    void refreshToken_ShouldReturnTokenInvalidResponse_WhenRefreshTokenIsInvalid() throws Exception {
        ResultActions actions = mockMvc.perform(post("/auth/refresh"));

        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(AuthResponse.TOKEN_REFRESH_SUCCESS.getHttpStatus().value()))
                .andExpect(jsonPath("$.message").value(AuthResponse.TOKEN_REFRESH_SUCCESS.getMessage()));

        verify(authService, times(1)).refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("로그아웃 - 성공")
    @WithMockUser(username = "testUser")
    void logout_ShouldReturnLogoutSuccessResponse_WhenCalled() throws Exception {
        // given
        String fakeJwt = "테스트용_JWT";
        Cookie accessTokenCookie = new Cookie("AccessToken", fakeJwt);
        String username = "testUser";

        // JWT 관련 Mock 설정
        given(cookieManager.extractAccessTokenFromHeader(any(HttpServletRequest.class)))
                .willReturn(Optional.of(fakeJwt));

        given(tokenValidator.isTokenExpired(fakeJwt))
                .willReturn(false);

        given(tokenExtractor.extractUsername(fakeJwt))
                .willReturn(username);

        UserDetails mockUserDetails = User.builder()
                .username(username)
                .password("password")
                .build();

        given(userDetailsService.loadUserByUsername(username))
                .willReturn(mockUserDetails);

        // logout 로직 Mock 설정
        given(authService.logout(any(HttpServletResponse.class)))
                .willReturn(AuthResponse.LOGOUT_SUCCESS);

        ResultActions actions = mockMvc.perform(post("/auth/logout").cookie(accessTokenCookie));

        actions
                .andDo(print())
                .andExpect(jsonPath("$.status").value(AuthResponse.LOGOUT_SUCCESS.getHttpStatus().value()))
                .andExpect(jsonPath("$.message").value(AuthResponse.LOGOUT_SUCCESS.getMessage()));

        verify(authService, times(1)).logout(any(HttpServletResponse.class));
    }
}
