package com.poortorich.auth.jwt.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.poortorich.auth.jwt.constants.JwtConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

@ExtendWith(MockitoExtension.class)
public class JwtTokenManagerTest {

    private final String TEST_ACCESS_TOKEN = "test-access-token";
    private final String TEST_REFRESH_TOKEN = "test-refresh-token";

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @InjectMocks
    private JwtTokenManager tokenManager;

    @Test
    @DisplayName("리프레시 토큰 쿠키 생성 - 올바른 속성을 가진 쿠키를 생성해야 함")
    void createRefreshCookie_ShouldCreateTokenCookieWithCorrectAttributes() {
        ResponseCookie cookie = tokenManager.createRefreshTokenCookie(TEST_REFRESH_TOKEN);

        assertThat(cookie.getName()).isEqualTo(JwtConstants.REFRESH_TOKEN_COOKIE_NAME);
        assertThat(cookie.getValue()).isEqualTo(TEST_REFRESH_TOKEN);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.getSameSite()).isEqualTo(JwtConstants.COOKIE_SAME_SITE);
        assertThat(cookie.getPath()).isEqualTo(JwtConstants.REFRESH_TOKEN_COOKIE_PATH);
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(JwtConstants.REFRESH_TOKEN_COOKIE_EXPIRY);
    }

    @Test
    @DisplayName("헤더에서 액세스 토큰 추출 - 액세스 토큰이 있는 경우 올바른 값을 반환해야 함")
    void extractAccessTokenFromCookies_ShouldReturnToken_WhenAccessTokenExists() {
        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER))
                .thenReturn(JwtConstants.TOKEN_PREFIX + TEST_ACCESS_TOKEN);

        Optional<String> result = tokenManager.extractAccessTokenFromHeader(request);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(TEST_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("쿠키에서 리프레시 토큰 추출 - 리프레시 토큰이 있는 경우 올바른 값을 반환해야 함")
    void extractRefreshTokenFromCookies_ShouldReturnToken_WhenRefreshTokenExists() {
        Cookie refreshTokenCookie = new Cookie(JwtConstants.REFRESH_TOKEN_COOKIE_NAME, TEST_REFRESH_TOKEN);
        Cookie otherCookie = new Cookie("other-cookie", "other-value");
        Cookie[] cookies = {otherCookie, refreshTokenCookie};

        when(request.getCookies()).thenReturn(cookies);

        Optional<String> result = tokenManager.extractRefreshTokenFromCookies(request);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(TEST_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("헤더에서 액세스 토큰 추출 - 액세스 토큰이 없는 경우 빈 Optional을 반환해야 함")
    void extractAccessTokenFromCookies_ShouldReturnEmpty_WhenAccessTokenNotExists() {
        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER)).thenReturn(null);

        Optional<String> result = tokenManager.extractAccessTokenFromHeader(request);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("쿠키에서 리프레시 토큰 추출 - 리프레시 토큰이 없는 경우 빈 Optional을 반환해야 함")
    void extractRefreshTokenFromCookies_ShouldReturnEmpty_WhenRefreshTokenNotExists() {
        Cookie otherCookie = new Cookie("other-cookie", "other-value");
        Cookie[] cookies = {otherCookie};

        when(request.getCookies()).thenReturn(cookies);

        Optional<String> result = tokenManager.extractRefreshTokenFromCookies(request);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("쿠키에서 토큰 추출 - 쿠키가 null인 경우 빈 Optional을 반환해야 함")
    void extractTokenFromCookies_ShouldReturnEmpty_WhenCookiesAreNull() {
        when(request.getCookies()).thenReturn(null);
        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER)).thenReturn(null);

        Optional<String> accessToken = tokenManager.extractAccessTokenFromHeader(request);
        Optional<String> refreshToken = tokenManager.extractRefreshTokenFromCookies(request);

        assertThat(accessToken).isEmpty();
        assertThat(refreshToken).isEmpty();
    }

    @Test
    @DisplayName("인증 쿠키 설정 - 헤더에 액세스 토큰과 쿠키에 리프레시 토큰을 추가해야 함")
    void setRefreshToken_ShouldAddTwoTokensHeadersToResponse() {
        tokenManager.setRefreshTokens(response, TEST_REFRESH_TOKEN);
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
    }

    @Test
    @DisplayName("인증 쿠키 삭제 - 헤더와 쿠키에서 ")
    void cleatAuthCookie_ShouldRemoveTokensFromResponse() {
        tokenManager.clearAuthTokens(response);

        verify(response).setHeader(JwtConstants.AUTHORIZATION_HEADER, "");
        verify(response, times(1)).addHeader(
                eq(HttpHeaders.SET_COOKIE),
                argThat(cookie ->
                        cookie.contains("Max-Age=0") && cookie.contains(JwtConstants.REFRESH_TOKEN_COOKIE_NAME)
                )
        );
    }
}
