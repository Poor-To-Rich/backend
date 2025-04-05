package com.poortorich.auth.util;

import com.poortorich.auth.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(JwtConstants.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public String getTokenFromCookies(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (Objects.equals(cookieName, cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public void addTokenCookiesToResponse(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addCookie(
                createSecureCookie(
                        JwtConstants.ACCESS_TOKEN_COOKIE_NAME,
                        accessToken,
                        (int) (JwtConstants.ACCESS_TOKEN_EXPIRATION_TIME / 1000)
                )
        );

        response.addCookie(
                createSecureCookie(
                        JwtConstants.REFRESH_TOKEN_COOKIE_NAME,
                        refreshToken,
                        (int) (JwtConstants.REFRESH_TOKEN_EXPIRATION_TIME / 1000)
                )
        );
    }

    public void clearTokenCookies(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie(JwtConstants.ACCESS_TOKEN_COOKIE_NAME, "");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath(JwtConstants.COOKIE_PATH);
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie(JwtConstants.REFRESH_TOKEN_COOKIE_NAME, "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath(JwtConstants.COOKIE_PATH);
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);
    }

    private Cookie createSecureCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(JwtConstants.COOKIE_PATH);
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
