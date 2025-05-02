package com.poortorich.email.enums;

import com.poortorich.email.constants.EmailResponseMessage;
import com.poortorich.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum EmailResponse implements Response {

    VERIFICATION_CODE_SENT(HttpStatus.OK, EmailResponseMessage.VERIFICATION_CODE_SENT),
    INVALID_PURPOSE(HttpStatus.BAD_REQUEST, EmailResponseMessage.PURPOSE_INVALID),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, EmailResponseMessage.VERIFICATION_CODE_INVALID),
    VERIFICATION_CODE_EXPIRED(HttpStatus.UNAUTHORIZED, EmailResponseMessage.VERIFICATION_CODE_EXPIRED),
    TOO_MANY_VERIFICATION_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, EmailResponseMessage.TOO_MANY_VERIFICATION_REQUESTS),
    VERIFICATION_CODE_REQUIRED(HttpStatus.BAD_REQUEST, EmailResponseMessage.VERIFICATION_CODE_REQUIRED),

    EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK, EmailResponseMessage.EMAIL_VERIFICATION_SUCCESS),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, EmailResponseMessage.EMAIL_INVALID),
    EMAIL_SEND_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, EmailResponseMessage.EMAIL_SEND_FAILURE),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, EmailResponseMessage.EMAIL_NOT_FOUND),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, EmailResponseMessage.EMAIL_NOT_VERIFIED),

    EMAIL_AUTH_REQUEST_BLOCKED(HttpStatus.TOO_MANY_REQUESTS, EmailResponseMessage.EMAIL_AUTH_REQUEST_BLOCKED),
    TOO_MANY_CODE_RESEND_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, EmailResponseMessage.TOO_MANY_CODE_RESEND_REQUESTS),

    DEFAULT(HttpStatus.INTERNAL_SERVER_ERROR, "내부 로직 예외 발생");

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
