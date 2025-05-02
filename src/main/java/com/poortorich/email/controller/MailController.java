package com.poortorich.email.controller;

import com.poortorich.email.facade.EmailFacade;
import com.poortorich.email.request.EmailVerificationRequest;
import com.poortorich.email.request.VerifyEmailCodeRequest;
import com.poortorich.email.util.EmailResponseMapper;
import com.poortorich.global.response.BaseResponse;
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
        return EmailResponseMapper.mapToResponseEntity(response, email, emailFacade);
    }

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse> verifyEmailCode(
            @RequestBody @Valid VerifyEmailCodeRequest verifyEmailCodeRequest
    ) {
        Response response = emailFacade.verifyEmailCode(verifyEmailCodeRequest);

        String email = verifyEmailCodeRequest.getEmail();
        return EmailResponseMapper.mapToResponseEntity(response, email, emailFacade);
    }
}
