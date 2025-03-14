package PoorToRich.PoorToRich.email.response;

import lombok.Data;

@Data
public class VerificationResendCodeStatusResponse {

    public static final String messageForm = "인증 코드 재발급 횟수 %d회 남았습니다.";

    private final Integer remainAttempts;
    private final String message;

    public VerificationResendCodeStatusResponse(int remainAttempts) {
        this.remainAttempts = remainAttempts;
        this.message = String.format(messageForm, remainAttempts);
    }
}
