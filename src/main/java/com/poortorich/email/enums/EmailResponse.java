package com.poortorich.email.enums;

import com.poortorich.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum EmailResponse implements Response {

    VERIFICATION_CODE_SENT("verification_code_sent", HttpStatus.OK, "인증 코드 전송 성공"),
    INVALID_PURPOSE("invalid_purpose", HttpStatus.BAD_REQUEST, "유효하지 않은 인증 목적입니다."),
    INVALID_VERIFICATION_CODE("invalid_verification_code", HttpStatus.BAD_REQUEST, "인증 코드가 올바르지 않습니다."),
    VERIFICATION_CODE_EXPIRED("verification_code_expired", HttpStatus.UNAUTHORIZED, "인증 코드가 만료되었습니다."),
    TOO_MANY_VERIFICATION_REQUESTS("too_many_verification_request", HttpStatus.TOO_MANY_REQUESTS, "인증 요청 횟수를 초과하였습니다."),
    VERIFICATION_CODE_REQUIRED("verification_code_required", HttpStatus.BAD_REQUEST, "인증 코드를 먼저 발급받으세요."),

    EMAIL_VERIFICATION_SUCCESS("email_verification_success", HttpStatus.OK, "이메일 인증 성공"),
    INVALID_EMAIL("invalid_email", HttpStatus.BAD_REQUEST, "잘못된 이메일 형식입니다."),
    EMAIL_SEND_FAILURE("mail_send_failure", HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),
    EMAIL_NOT_FOUND("mail_not_found", HttpStatus.NOT_FOUND, "존재하지 않는 이메일 주소입니다."),
    EMAIL_NOT_VERIFIED("mail_not_verified", HttpStatus.FORBIDDEN, "인증되지 않은 이메일입니다."),
    TOO_MANY_REQUEST_MAIL("too_many_request_mail", HttpStatus.TOO_MANY_REQUESTS, "인증 요청 횟수를 초과하였습니다. 30분 후에 다시 시도하세요."),

    TOO_MANY_CODE_RESEND_REQUESTS(
            "too_many_code_resend_requests",
            HttpStatus.TOO_MANY_REQUESTS,
            "인증 코드 재발급 횟수를 초과하였습니다."
    ),

    DEFAULT("default_email_exception", HttpStatus.INTERNAL_SERVER_ERROR, "내부 로직 예외 발생");

    private final String identifier;
    private final HttpStatus httpStatus;
    private final String message;

    public static Response getResponseByIdentifier(String identifier) {
        for (EmailResponse response : EmailResponse.values()) {
            if (response.identifier.equals(identifier)) {
                return response;
            }
        }
        return EmailResponse.DEFAULT;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
