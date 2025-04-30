package com.poortorich.email.constants;

public class EmailResponseMessage {

    public static final String VERIFICATION_CODE_SENT = "인증 코드 전송 성공";
    public static final String INVALID_PURPOSE = "유효하지 않은 인증 목적입니다.";
    public static final String INVALID_VERIFICATION_CODE = "인증 코드가 올바르지 않습니다.";
    public static final String VERIFICATION_CODE_EXPIRED = "인증 코드가 만료되었습니다.";
    public static final String TOO_MANY_VERIFICATION_REQUESTS = "인증 시도 횟수를 초과하였습니다.";
    public static final String VERIFICATION_CODE_REQUIRED = "인증 코드를 먼저 발급받으세요.";
    public static final String EMAIL_VERIFICATION_SUCCESS = "이메일 인증 성공";
    public static final String INVALID_EMAIL = "잘못된 이메일 형식입니다.";
    public static final String EMAIL_SEND_FAILURE = "이메일 전송에 실패하였습니다.";
    public static final String EMAIL_NOT_FOUND = "존재하지 않는 이메일 주소입니다.";
    public static final String EMAIL_NOT_VERIFIED = "인증되지 않은 이메일입니다.";
    public static final String TOO_MANY_CODE_RESEND_REQUESTS = "인증 코드 재발급 횟수를 초과하였습니다.";
    public static final String EMAIL_AUTH_REQUEST_BLOCKED = "이메일 인증 요청이 너무 많아 일시적으로 차단되었습니다.";

    public static final String BLOCK_RETRY_MESSAGE_TEMPLATE = "약 %d분 뒤에 다시 시도해주세요.";
    public static final String VERIFICATION_ATTEMPTS_MESSAGE_TEMPLATE = "남은 이메일 인증 횟수: %d회";
    public static final String RESEND_CODE_ATTEMPTS_MESSAGE_TEMPLATE = "남은 인증 코드 발급 횟수: %d회";

    private EmailResponseMessage() {
    }
}
