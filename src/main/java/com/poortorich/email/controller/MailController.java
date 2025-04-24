package com.poortorich.email.controller;

import com.poortorich.email.constants.EmailResponseMessage;
import com.poortorich.email.facade.EmailFacade;
import com.poortorich.email.request.EmailBlockTimeResponse;
import com.poortorich.email.request.EmailVerificationRequest;
import com.poortorich.email.request.VerifyEmailCodeRequest;
import com.poortorich.email.response.VerificationAttemptStatusResponse;
import com.poortorich.email.response.VerificationResendCodeStatusResponse;
import com.poortorich.email.validator.annotations.Email;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.Response;
import jakarta.validation.Valid;
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
            @Email
            @NotBlank(message = EmailResponseMessage.INVALID_EMAIL)
            String email
    ) {
        VerificationResendCodeStatusResponse verificationCodeStatus = emailFacade.getResendStatus(email);
        return ResponseEntity.ok(verificationCodeStatus);
    }

    @GetMapping("/verify")
    public ResponseEntity<VerificationAttemptStatusResponse> getAttpemtStatus(
            @RequestParam
            @Valid
            @Email
            @NotBlank(message = EmailResponseMessage.INVALID_EMAIL)
            String email
    ) {
        VerificationAttemptStatusResponse verificationAttemptStatus = emailFacade.getAttemptStatus(email);
        return ResponseEntity.ok(verificationAttemptStatus);
    }

    @GetMapping("/block")
    public ResponseEntity<EmailBlockTimeResponse> getBlockTime(
            @RequestParam
            @Valid
            @Email
            @NotBlank(message = EmailResponseMessage.INVALID_EMAIL)
            String email
    ) {
        EmailBlockTimeResponse emailBlockTimeResponse = emailFacade.getBlockTime(email);
        return ResponseEntity.ok(emailBlockTimeResponse);
    }
}
