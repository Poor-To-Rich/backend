package com.poortorich.auth.jwt.constants;

public class JwtConstants {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REDIS_REFRESH_TOKEN_KEY = "auth:refresh-token:user:%d:token:%s";
    public static final String REDIS_REFRESH_TOKEN_SCAN_KEY = "auth:refresh-token:user:%d:*";

    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 30;
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final int ACCESS_TOKEN_COOKIE_EXPIRY = 30 * 60;
    public static final int REFRESH_TOKEN_COOKIE_EXPIRY = 7 * 24 * 60 * 60;
    public static final String COOKIE_SAME_SITE = "Strict";
    public static final String ACCESS_TOKEN_COOKIE_PATH = "/";
    public static final String REFRESH_TOKEN_COOKIE_PATH = "/auth/refresh";

    public static final String TOKEN_ISSUER = "poorToRich";
    public static final String TOKEN_AUDIENCE = "poorToRich-Users";

    private JwtConstants() {
    }
}
