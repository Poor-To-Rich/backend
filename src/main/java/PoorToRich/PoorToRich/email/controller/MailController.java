package PoorToRich.PoorToRich.email.controller;

import PoorToRich.PoorToRich.email.facade.EmailFacade;
import PoorToRich.PoorToRich.email.request.EmailVerificationRequest;
import PoorToRich.PoorToRich.email.request.VerifyEmailCodeRequest;
import PoorToRich.PoorToRich.global.response.BaseResponse;
import PoorToRich.PoorToRich.global.response.Response;
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
        return BaseResponse.toResponseEntity(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse> verifyEmailCode(
            @RequestBody @Valid VerifyEmailCodeRequest verifyEmailCodeRequest
    ) {
        Response response = emailFacade.verifyEmailCode(verifyEmailCodeRequest);
        return BaseResponse.toResponseEntity(response);
    }
}
