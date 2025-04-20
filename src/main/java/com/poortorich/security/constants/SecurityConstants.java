package com.poortorich.security.constants;

import java.util.List;

public class SecurityConstants {

    public static final List<String> ALLOWED_ORIGINS = List.of("http://localhost:8080",
            "http://localhost:5173",
            "https://poortorich.site");
    public static final List<String> ALLOWED_METHOD = List.of("GET", "POST", "PUT", "DELETE");
    public static final List<String> ALLOWED_HEADERS = List.of("authorization", "content-type", "x-auth-token");
    public static final String[] PERMIT_ALL_ENDPOINTS = {
            "/auth/health",
            "/auth/login",
            "/auth/refresh",
            "/auth/register",
            "/user/exists/username",
            "/user/exists/nickname",
            "/email/send",
            "/email/verify",
            "/email/block"
    };
    public static final String CORS_ALL_PATH = "/**";

    private SecurityConstants() {
    }
}
