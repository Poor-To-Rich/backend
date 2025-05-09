package com.poortorich.auth.response.enums;

import com.poortorich.auth.constants.AuthResponseMessage;
import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum AuthResponse implements Response {
    HEALTH_CHECK(HttpStatus.OK, AuthResponseMessage.HEALTH_CHECK, null),

    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, AuthResponseMessage.TOKEN_INVALID, null),
    CREDENTIALS_INVALID(HttpStatus.BAD_REQUEST, AuthResponseMessage.CREDENTIALS_INVALID, null),
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, AuthResponseMessage.TOKEN_REFRESH_SUCCESS, null),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, AuthResponseMessage.ACCESS_TOKEN_EXPIRED, null),

    LOGIN_SUCCESS(HttpStatus.OK, AuthResponseMessage.LOGIN_SUCCESS, null),
    LOGOUT_SUCCESS(HttpStatus.OK, AuthResponseMessage.LOGOUT_SUCCESS, null),

    REDIS_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, AuthResponseMessage.REDIS_SERVER_EXCEPTION, null);

    private final HttpStatus httpStatus;
    private final String message;
    private final String field;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getField() {
        return field;
    }
}
