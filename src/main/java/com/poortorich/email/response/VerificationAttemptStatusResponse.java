package com.poortorich.email.response;

public class VerificationAttemptStatusResponse {

    public static final String messageForm = "이메일 인증 시도 횟수 %d회 남았습니다.";

    private final int remainAttempts;
    private final String message;

    public VerificationAttemptStatusResponse(int remainAttempts) {
        this.remainAttempts = remainAttempts;
        this.message = String.format(messageForm, remainAttempts);
    }
}
