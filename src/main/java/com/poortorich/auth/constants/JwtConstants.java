package com.poortorich.auth.constants;

public class JwtConstants {

    // 1시간
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 60 * 60 * 1000L;
    // 7일
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;

    public static final String SECRET_KEY = "{jwt.secret.key}";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";

    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private JwtConstants() {
    }
}
