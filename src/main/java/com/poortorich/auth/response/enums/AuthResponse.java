package com.poortorich.auth.jwt.response.enums;

import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum AuthResponse implements Response {
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "인증 정보가 유효하지 않습니다. 다시 로그인해 주세요."),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
