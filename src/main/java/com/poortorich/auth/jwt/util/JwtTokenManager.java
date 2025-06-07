package com.poortorich.auth.jwt.util;

import com.poortorich.auth.jwt.constants.JwtConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtTokenManager {

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(JwtConstants.REFRESH_TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(true)
                .sameSite(JwtConstants.COOKIE_SAME_SITE)
                .domain("localhost")
                .path(JwtConstants.REFRESH_TOKEN_COOKIE_PATH)
                .maxAge(JwtConstants.REFRESH_TOKEN_COOKIE_EXPIRY)
                .build();
    }

    private ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(JwtConstants.REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite(JwtConstants.COOKIE_SAME_SITE)
                .domain("localhost")
                .path(JwtConstants.REFRESH_TOKEN_COOKIE_PATH)
                .maxAge(0)
                .build();
    }

    public Optional<String> extractAccessTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader(JwtConstants.AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return Optional.of(header.substring(JwtConstants.TOKEN_PREFIX.length()));
        }
        return Optional.empty();
    }

    public Optional<String> extractRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return extractTokenFromCookies(cookies, JwtConstants.REFRESH_TOKEN_COOKIE_NAME);
        }

        return Optional.empty();
    }

    private Optional<String> extractTokenFromCookies(Cookie[] cookies, String cookieName) {
        for (Cookie cookie : cookies) {
            if (Objects.equals(cookie.getName(), cookieName)) {
                return Optional.of(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    public void setRefreshTokens(HttpServletResponse response, String refreshToken) {
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    public void clearAuthTokens(HttpServletResponse response) {
        response.setHeader(JwtConstants.AUTHORIZATION_HEADER, "");

        ResponseCookie deleteRefreshToken = deleteRefreshTokenCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshToken.toString());
    }
}
