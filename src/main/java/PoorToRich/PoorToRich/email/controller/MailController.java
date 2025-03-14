package PoorToRich.PoorToRich.email.controller;

import PoorToRich.PoorToRich.email.facade.EmailFacade;
import PoorToRich.PoorToRich.email.request.EmailVerificationRequest;
import PoorToRich.PoorToRich.email.request.VerifyEmailCodeRequest;
import PoorToRich.PoorToRich.email.response.VerificationResendCodeStatusResponse;
import PoorToRich.PoorToRich.global.response.BaseResponse;
import PoorToRich.PoorToRich.global.response.Response;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class MailController {

    private final EmailFacade emailFacade;

    @PostMapping("/send")
    public ResponseEntity<BaseResponse> sendVerificationCode(
            @RequestBody @Valid EmailVerificationRequest emailVerificationRequest
    ) {
        Response response = emailFacade.sendVerificationCode(emailVerificationRequest);
        return BaseResponse.toResponseEntity(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse> verifyEmailCode(
            @RequestBody @Valid VerifyEmailCodeRequest verifyEmailCodeRequest
    ) {
        Response response = emailFacade.verifyEmailCode(verifyEmailCodeRequest);
        return BaseResponse.toResponseEntity(response);
    }

    @GetMapping("/send")
    public ResponseEntity<VerificationResendCodeStatusResponse> getResendStatus(
            @RequestParam
            @Valid
            @Email(message = "invalid_mail")
            @NotBlank(message = "invalid_mail")
            String email
    ) {
        VerificationResendCodeStatusResponse verificationCodeStatus = emailFacade.getResendStatus(email);
        return ResponseEntity.ok(verificationCodeStatus);
    }
}
