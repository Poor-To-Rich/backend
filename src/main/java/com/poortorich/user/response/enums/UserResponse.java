package com.poortorich.user.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.user.constants.UserConstant.ValidationMessages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserResponse implements Response {

    REGISTRATION_SUCCESS(HttpStatus.CREATED, "회원 가입 성공"),

    USERNAME_DUPLICATE(HttpStatus.CONFLICT, ValidationMessages.USERNAME_DUPLICATE),
    NICKNAME_DUPLICATE(HttpStatus.CONFLICT, ValidationMessages.NICKNAME_DUPLICATE),
    EMAIL_DUPLICATE(HttpStatus.CONFLICT, ValidationMessages.EMAIL_DUPLICATE),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, ValidationMessages.PASSWORD_DO_NOT_MATCH);

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
