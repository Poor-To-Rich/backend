package com.poortorich.auth.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.poortorich.auth.jwt.fixture.JwtUserFixture;
import com.poortorich.auth.jwt.util.JwtCookieManager;
import com.poortorich.auth.jwt.util.JwtTokenExtractor;
import com.poortorich.auth.jwt.util.JwtTokenGenerator;
import com.poortorich.auth.repository.interfaces.RefreshTokenRepository;
import com.poortorich.auth.request.LoginRequest;
import com.poortorich.auth.util.LoginRequestTestBuilder;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthService {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenGenerator tokenGenerator;

    @Mock
    private JwtTokenExtractor tokenExtractor;

    @Mock
    private JwtCookieManager cookieManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse resposne;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private User testUser;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequestTestBuilder.builder().build();

        testUser = mock(User.class);
        when(testUser.getId()).thenReturn(JwtUserFixture.TEST_ID);
        when(testUser.getUsername()).thenReturn(JwtUserFixture.TEST_USERNAME);
        when(testUser.getPassword()).thenReturn(JwtUserFixture.TEST_ENCODED_PASSWORD);

        accessToken = "test-access-token";
        refreshToken = "test-refresh-token";
    }

    @Test
    @DisplayName("로그인 성공 - 인증정보가 일치하면 토큰을 생성하고 쿠키를 설정해야 함")
    void login_ShouldGenerateTokensAndSetCookies_WhenCredentialsMatch() {
        when(userDetailsService.loadUserByUsername(loginRequest.getUsername())).thenReturn(testUser);
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(true);
        when(tokenGenerator.generateAccessToken(testUser)).thenReturn(accessToken);
        when(tokenGenerator.generateRefreshToken(testUser)).thenReturn(refreshToken);
    }
}
