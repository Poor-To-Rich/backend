package com.poortorich.auth.response.enums;

import com.poortorich.auth.constants.AuthResponseMessage;
import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum AuthResponse implements Response {

    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, AuthResponseMessage.TOKEN_INVALID),
    CREDENTIALS_INVALID(HttpStatus.BAD_REQUEST, AuthResponseMessage.CREDENTIALS_INVALID),
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, AuthResponseMessage.TOKEN_REFRESH_SUCCESS),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, AuthResponseMessage.ACCESS_TOKEN_EXPIRED),

    LOGIN_SUCCESS(HttpStatus.OK, AuthResponseMessage.LOGIN_SUCCESS),
    LOGOUT_SUCCESS(HttpStatus.OK, AuthResponseMessage.LOGOUT_SUCCESS),

    REDIS_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, AuthResponseMessage.REDIS_SERVER_EXCEPTION);

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
