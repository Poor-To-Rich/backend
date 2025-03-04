package PoorToRich.PoorToRich.email.enums;

import PoorToRich.PoorToRich.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum EmailResponse implements Response {
    VERIFICATION_CODE_SENT(HttpStatus.OK, "인증 코드 전송 성공");

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
