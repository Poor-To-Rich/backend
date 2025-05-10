package com.poortorich.user.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.user.constants.UserResponseMessages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserResponse implements Response {

    REGISTRATION_SUCCESS(HttpStatus.CREATED, UserResponseMessages.REGISTRATION_SUCCESS),

    USERNAME_DUPLICATE(HttpStatus.CONFLICT, UserResponseMessages.USERNAME_DUPLICATE),
    USERNAME_AVAILABLE(HttpStatus.OK, UserResponseMessages.USERNAME_AVAILABLE),
    USERNAME_RESERVE_CHECK_REQUIRED(HttpStatus.BAD_REQUEST, UserResponseMessages.USERNAME_RESERVE_CHECK_REQUIRED),
    NICKNAME_DUPLICATE(HttpStatus.CONFLICT, UserResponseMessages.NICKNAME_DUPLICATE),
    NICKNAME_AVAILABLE(HttpStatus.OK, UserResponseMessages.NICKNAME_AVAILABLE),
    NICKNAME_RESERVE_CHECK_REQUIRED(HttpStatus.BAD_REQUEST, UserResponseMessages.NICKNAME_RESERVE_CHECK_REQUIRED),
    EMAIL_DUPLICATE(HttpStatus.CONFLICT, UserResponseMessages.EMAIL_DUPLICATE),
    PASSWORD_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, UserResponseMessages.PASSWORD_DO_NOT_MATCH),
    BIRTHDAY_IN_FUTURE(HttpStatus.BAD_REQUEST, UserResponseMessages.BIRTHDAY_IN_FUTURE),
    GENDER_INVALID(HttpStatus.BAD_REQUEST, UserResponseMessages.GENDER_INVALID),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, UserResponseMessages.USER_NOT_FOUND),
    USER_DETAIL_FIND_SUCCESS(HttpStatus.OK, UserResponseMessages.USER_DETAIL_FIND_SUCCESS),
    USER_PROFILE_UPDATE_SUCCESS(HttpStatus.OK, UserResponseMessages.USER_PROFILE_UPDATE_SUCCESS),
    USER_EMAIL_FIND_SUCCESS(HttpStatus.OK, UserResponseMessages.USER_EMAIL_FIND_SUCCESS);

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
