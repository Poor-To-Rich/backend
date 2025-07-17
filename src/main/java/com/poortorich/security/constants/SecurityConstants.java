package com.poortorich.security.constants;

import java.util.List;

public class SecurityConstants {

    public static final List<String> ALLOWED_ORIGINS = List.of("http://localhost:8080",
            "https://localhost:5173",
            "https://poortorich.site",
            "https://www.poortorich.site"
            // STOMP 연결 테스트 사이트
            //"https://jiangxy.github.io"
    );
    public static final List<String> ALLOWED_METHOD = List.of("GET", "POST", "PUT", "DELETE");
    public static final List<String> ALLOWED_HEADERS = List.of("authorization", "content-type", "x-auth-token");
    public static final String[] PERMIT_ALL_ENDPOINTS = {
            "/auth/health",
            "/auth/login",
            "/auth/refresh",
            "/user/register",
            "/user/exists/username",
            "/user/exists/nickname",
            "/email/send",
            "/email/verify",
            "/email/block",
            "/user/username-recovery",
            "/user/reset-password",
            "/chat-websocket/**",
            "/chat/**"
    };
    public static final String CORS_ALL_PATH = "/**";

    private SecurityConstants() {
    }
}
