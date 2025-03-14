package com.poortorich.email.facade;

import com.poortorich.email.enums.EmailResponse;
import com.poortorich.email.request.EmailVerificationRequest;
import com.poortorich.email.request.VerifyEmailCodeRequest;
import com.poortorich.email.service.EmailVerificationService;
import com.poortorich.email.service.MailService;
import com.poortorich.email.util.VerificationCodeGenerator;
import com.poortorich.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailFacade {

    private final MailService mailService;
    private final EmailVerificationService verificationService;
    private final VerificationCodeGenerator codeGenerator;

    @Transactional
    public Response sendVerificationCode(EmailVerificationRequest emailVerificationRequest) {
        String toEmail = emailVerificationRequest.getEmail();
        String verificationPurpose = emailVerificationRequest.getPurpose();
        String verificationCode = codeGenerator.generate();

        verificationService.saveCode(toEmail, verificationPurpose, verificationCode);
        mailService.sendEmail(toEmail, verificationPurpose, verificationCode);

        return EmailResponse.VERIFICATION_CODE_SENT;
    }


    @Transactional
    public Response verifyEmailCode(VerifyEmailCodeRequest verifyEmailCodeRequest) {
        String toMail = verifyEmailCodeRequest.getEmail();
        String verificationPurpose = verifyEmailCodeRequest.getPurpose();
        String verificationCode = verifyEmailCodeRequest.getVerificationCode();

        if (!verificationService.verifyCode(toMail, verificationPurpose, verificationCode)) {
            return EmailResponse.INVALID_VERIFICATION_CODE;
        }
        return EmailResponse.EMAIL_VERIFICATION_SUCCESS;
    }
}
