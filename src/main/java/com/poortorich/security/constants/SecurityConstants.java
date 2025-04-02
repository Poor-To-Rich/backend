package com.poortorich.security.constants;

import java.util.List;

public class SecurityConstants {

    public static final List<String> ALLOWED_ORIGINS = List.of("http://localhost:8080");
    public static final List<String> ALLOWED_METHOD = List.of("GET", "POST", "PUT", "DELETE");
    public static final List<String> ALLOWED_HEADERS = List.of("authorization", "content-type", "x-auth-token");

    public static final String CORS_ALL_PATH = "/**";

    private SecurityConstants() {
    }
}
