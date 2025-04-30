package com.poortorich.email.controller;

import com.poortorich.email.enums.EmailResponse;
import com.poortorich.email.facade.EmailFacade;
import com.poortorich.email.request.EmailVerificationRequest;
import com.poortorich.email.request.VerifyEmailCodeRequest;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.global.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

        String email = emailVerificationRequest.getEmail();
        if (response == EmailResponse.EMAIL_AUTH_REQUEST_BLOCKED) {
            return DataResponse.toResponseEntity(response, emailFacade.getBlockExpired(email));
        }
        if (response == EmailResponse.TOO_MANY_CODE_RESEND_REQUESTS) {
            return BaseResponse.toResponseEntity(response);
        }
        return DataResponse.toResponseEntity(response, emailFacade.getResendCodeAttempts(email));
    }

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse> verifyEmailCode(
            @RequestBody @Valid VerifyEmailCodeRequest verifyEmailCodeRequest
    ) {
        Response response = emailFacade.verifyEmailCode(verifyEmailCodeRequest);

        String email = verifyEmailCodeRequest.getEmail();
        if (response == EmailResponse.EMAIL_AUTH_REQUEST_BLOCKED) {
            return DataResponse.toResponseEntity(response, emailFacade.getBlockExpired(email));
        }
        if (response == EmailResponse.TOO_MANY_VERIFICATION_REQUESTS) {
            return BaseResponse.toResponseEntity(response);
        }
        return DataResponse.toResponseEntity(response, emailFacade.getEmailVerificationAttempts(email));
    }
}
