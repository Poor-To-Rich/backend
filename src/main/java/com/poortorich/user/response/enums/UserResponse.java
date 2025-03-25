package com.poortorich.user.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.user.constants.UserResponseMessages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserResponse implements Response {

    REGISTRATION_SUCCESS(HttpStatus.CREATED, UserResponseMessages.REGISTRATION_SUCCESS),

    USERNAME_DUPLICATE(HttpStatus.CONFLICT, UserResponseMessages.USERNAME_DUPLICATE),
    NICKNAME_DUPLICATE(HttpStatus.CONFLICT, UserResponseMessages.NICKNAME_DUPLICATE),
    EMAIL_DUPLICATE(HttpStatus.CONFLICT, UserResponseMessages.EMAIL_DUPLICATE),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, UserResponseMessages.PASSWORD_DO_NOT_MATCH);

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
