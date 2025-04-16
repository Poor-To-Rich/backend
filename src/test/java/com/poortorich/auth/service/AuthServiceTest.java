package com.poortorich.auth.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.poortorich.auth.jwt.fixture.JwtUserFixture;
import com.poortorich.auth.jwt.util.JwtCookieManager;
import com.poortorich.auth.jwt.util.JwtTokenExtractor;
import com.poortorich.auth.jwt.util.JwtTokenGenerator;
import com.poortorich.auth.jwt.validator.JwtTokenValidator;
import com.poortorich.auth.repository.interfaces.RefreshTokenRepository;
import com.poortorich.auth.request.LoginRequest;
import com.poortorich.auth.response.enums.AuthResponse;
import com.poortorich.auth.util.LoginRequestTestBuilder;
import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.global.exceptions.UnauthorizedException;
import com.poortorich.global.response.Response;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

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
    private JwtTokenValidator tokenValidator;

    @Mock
    private JwtCookieManager cookieManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private User testUser;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequestTestBuilder.builder()
                .username(JwtUserFixture.TEST_USERNAME)
                .build();

        testUser = mock(User.class);
        lenient().when(testUser.getId()).thenReturn(JwtUserFixture.TEST_ID);
        lenient().when(testUser.getUsername()).thenReturn(JwtUserFixture.TEST_USERNAME);
        lenient().when(testUser.getPassword()).thenReturn(JwtUserFixture.TEST_ENCODED_PASSWORD);

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

        Response result = authService.login(loginRequest, response);

        assertThat(result).isEqualTo(AuthResponse.LOGIN_SUCCESS);
        verify(refreshTokenRepository).save(testUser.getId(), refreshToken);
        verify(cookieManager).setAuthCookie(response, accessToken, refreshToken);
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 패스워트로 인증 실패 응답을 반환해야 함")
    void login_ShouldReturnCredentialsInvalid_WhenPasswordDoesNotMatches() {
        when(userDetailsService.loadUserByUsername(loginRequest.getUsername())).thenReturn(testUser);
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(false);

        Response result = authService.login(loginRequest, response);

        assertThat(result).isEqualTo(AuthResponse.CREDENTIALS_INVALID);
        verifyNoInteractions(tokenGenerator, cookieManager);
        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    @DisplayName("토큰 갱신 성공 - 유효한 리프레시 토큰으로 새로운 토큰을 생성하고 쿠키를 설정해야 함")
    void refreshToken_ShouldGenerateNewTokensAndSetCookies_WhenRefreshTokenIsValid() {
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        when(cookieManager.extractRefreshTokenFromCookies(request)).thenReturn(Optional.of(refreshToken));
        when(tokenExtractor.extractUsername(refreshToken)).thenReturn(JwtUserFixture.TEST_USERNAME);
        when(userRepository.findByUsername(JwtUserFixture.TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(tokenGenerator.generateAccessToken(testUser)).thenReturn(newAccessToken);
        when(tokenGenerator.generateRefreshToken(testUser)).thenReturn(newRefreshToken);

        Response result = authService.refreshToken(request, response);

        assertThat(result).isEqualTo(AuthResponse.TOKEN_REFRESH_SUCCESS);
        verify(tokenValidator).validateToken(refreshToken);
        verify(refreshTokenRepository).deleteByUserIdAndToken(testUser.getId(), refreshToken);
        verify(refreshTokenRepository).save(testUser.getId(), newRefreshToken);
        verify(cookieManager).setAuthCookie(response, newAccessToken, newRefreshToken);
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 리프레시 토큰이 없으면 실패 응답을 반환해야 함")
    void refreshToken_ShouldReturnTokenInvalid_WhenRefreshTokenIsNotPresent() {
        when(cookieManager.extractRefreshTokenFromCookies(request)).thenReturn(Optional.empty());

        Response result = authService.refreshToken(request, response);

        assertThat(result).isEqualTo(AuthResponse.TOKEN_INVALID);
        verifyNoInteractions(tokenValidator, tokenGenerator, userRepository);
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 사용자를 찾을 수 없으면 예외를 발생시켜야 함")
    void refreshToken_ShouldThrowException_WhenUserIsNotFound() {
        when(cookieManager.extractRefreshTokenFromCookies(request)).thenReturn(Optional.of(refreshToken));
        when(tokenExtractor.extractUsername(refreshToken)).thenReturn(JwtUserFixture.TEST_USERNAME);
        when(userRepository.findByUsername(JwtUserFixture.TEST_USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken(request, response))
                .isInstanceOf(UnauthorizedException.class)
                .extracting(e -> ((UnauthorizedException) e).getResponse())
                .isEqualTo(AuthResponse.TOKEN_INVALID);

        verify(tokenValidator).validateToken(refreshToken);
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 데이터베이스 액세스 오류 시 서버 에러 예외를 발생시켜야 함")
    void refreshToken_ShouldThrowServerException_WhenDataAccessError() {
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        when(cookieManager.extractRefreshTokenFromCookies(request)).thenReturn(Optional.of(refreshToken));
        when(tokenExtractor.extractUsername(refreshToken)).thenReturn(JwtUserFixture.TEST_USERNAME);
        when(userRepository.findByUsername(JwtUserFixture.TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(tokenGenerator.generateAccessToken(testUser)).thenReturn(newAccessToken);
        when(tokenGenerator.generateRefreshToken(testUser)).thenReturn(newRefreshToken);
        doThrow(new DataAccessException("Database error") {
        }).when(refreshTokenRepository).deleteByUserIdAndToken(JwtUserFixture.TEST_ID, refreshToken);

        assertThatThrownBy(() -> authService.refreshToken(request, response))
                .isInstanceOf(InternalServerErrorException.class)
                .extracting(e -> ((InternalServerErrorException) e).getResponse())
                .isEqualTo(AuthResponse.REDIS_SERVER_ERROR);

        verify(tokenValidator).validateToken(refreshToken);
    }

    @Test
    @DisplayName("로그아웃 성공 - 인증 쿠키를 삭제해야 함")
    void logout_ShouldClearAuthCookies() {
        Response result = authService.logout(response);

        assertThat(result).isEqualTo(AuthResponse.LOGOUT_SUCCESS);
        verify(cookieManager).clearAuthCookie(response);
    }
}
