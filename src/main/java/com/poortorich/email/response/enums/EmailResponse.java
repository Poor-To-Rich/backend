package com.poortorich.email.response.enums;

import com.poortorich.email.constants.EmailResponseMessage;
import com.poortorich.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum EmailResponse implements Response {

    VERIFICATION_CODE_SENT(HttpStatus.OK, EmailResponseMessage.VERIFICATION_CODE_SENT, null),
    PURPOSE_INVALID(HttpStatus.BAD_REQUEST, EmailResponseMessage.PURPOSE_INVALID, "purpose"),
    VERIFICATION_CODE_INVALID(HttpStatus.BAD_REQUEST, EmailResponseMessage.VERIFICATION_CODE_INVALID, "verificationCode"),
    VERIFICATION_CODE_EXPIRED(HttpStatus.UNAUTHORIZED, EmailResponseMessage.VERIFICATION_CODE_EXPIRED, "verificationCode"),
    TOO_MANY_VERIFICATION_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, EmailResponseMessage.TOO_MANY_VERIFICATION_REQUESTS, null),
    VERIFICATION_CODE_REQUIRED(HttpStatus.BAD_REQUEST, EmailResponseMessage.VERIFICATION_CODE_REQUIRED, "verificationCode"),

    EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK, EmailResponseMessage.EMAIL_VERIFICATION_SUCCESS, null),
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, EmailResponseMessage.EMAIL_INVALID, "email"),
    EMAIL_SEND_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, EmailResponseMessage.EMAIL_SEND_FAILURE, null),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, EmailResponseMessage.EMAIL_NOT_FOUND, "email"),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, EmailResponseMessage.EMAIL_NOT_VERIFIED, "email"),

    EMAIL_AUTH_REQUEST_BLOCKED(HttpStatus.TOO_MANY_REQUESTS, EmailResponseMessage.EMAIL_AUTH_REQUEST_BLOCKED, null),
    TOO_MANY_CODE_RESEND_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, EmailResponseMessage.TOO_MANY_CODE_RESEND_REQUESTS, null),

    DEFAULT(HttpStatus.INTERNAL_SERVER_ERROR, "내부 로직 예외 발생", null);

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
