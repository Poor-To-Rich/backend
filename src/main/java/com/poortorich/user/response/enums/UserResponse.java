package com.poortorich.user.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.user.constants.UserResponseMessages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserResponse implements Response {

    REGISTRATION_SUCCESS(HttpStatus.CREATED, UserResponseMessages.REGISTRATION_SUCCESS, null),

    USERNAME_DUPLICATE(HttpStatus.CONFLICT, UserResponseMessages.USERNAME_DUPLICATE, "username"),
    USERNAME_AVAILABLE(HttpStatus.OK, UserResponseMessages.USERNAME_AVAILABLE, null),
    USERNAME_RESERVE_CHECK_REQUIRED(HttpStatus.BAD_REQUEST, UserResponseMessages.USERNAME_RESERVE_CHECK_REQUIRED,
            "username"),

    NICKNAME_DUPLICATE(HttpStatus.CONFLICT, UserResponseMessages.NICKNAME_DUPLICATE, "nickname"),
    NICKNAME_AVAILABLE(HttpStatus.OK, UserResponseMessages.NICKNAME_AVAILABLE, null),
    NICKNAME_RESERVE_CHECK_REQUIRED(HttpStatus.BAD_REQUEST, UserResponseMessages.NICKNAME_RESERVE_CHECK_REQUIRED,
            "nickname"),

    EMAIL_DUPLICATE(HttpStatus.CONFLICT, UserResponseMessages.EMAIL_DUPLICATE, "email"),

    PASSWORD_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, UserResponseMessages.PASSWORD_DO_NOT_MATCH, "password"),
    PASSWORD_UPDATE_SUCCESS(HttpStatus.OK, UserResponseMessages.PASSWORD_UPDATE_SUCCESS, null),
    CURRENT_PASSWORD_IS_WRONG(HttpStatus.BAD_REQUEST, UserResponseMessages.CURRENT_PASSWORD_IS_WRONG,
            "currentPassword"),
    NEW_PASSWORD_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, UserResponseMessages.NEW_PASSWORD_IS_DO_NOT_MATCH, "newPassword"),

    BIRTHDAY_IN_FUTURE(HttpStatus.BAD_REQUEST, UserResponseMessages.BIRTHDAY_IN_FUTURE, "birth"),

    GENDER_INVALID(HttpStatus.BAD_REQUEST, UserResponseMessages.GENDER_INVALID, "gender"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, UserResponseMessages.USER_NOT_FOUND, null),
    USER_DETAIL_FIND_SUCCESS(HttpStatus.OK, UserResponseMessages.USER_DETAIL_FIND_SUCCESS, null),
    USER_EMAIL_FIND_SUCCESS(HttpStatus.OK, UserResponseMessages.USER_EMAIL_FIND_SUCCESS, null),
    USER_PROFILE_UPDATE_SUCCESS(HttpStatus.OK, UserResponseMessages.USER_PROFILE_UPDATE_SUCCESS, null),
    USER_EMAIL_UPDATE_SUCCESS(HttpStatus.OK, UserResponseMessages.USER_EMAIL_UPDATE_SUCCESS, null),
    RESET_SUCCESS(HttpStatus.OK, UserResponseMessages.RESET_SUCCESS, null);

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
