package com.poortorich.email.facade;

import com.poortorich.email.constants.EmailResponseMessage;
import com.poortorich.email.enums.EmailResponse;
import com.poortorich.email.enums.EmailVerificationType;
import com.poortorich.email.request.EmailVerificationRequest;
import com.poortorich.email.request.VerifyEmailCodeRequest;
import com.poortorich.email.response.EmailPolicyResponse;
import com.poortorich.email.service.EmailVerificationService;
import com.poortorich.email.service.MailService;
import com.poortorich.email.util.EmailVerificationPolicyManager;
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
    private final EmailVerificationPolicyManager verificationPolicyManager;

    @Transactional
    public Response sendVerificationCode(EmailVerificationRequest emailVerificationRequest) {
        String email = emailVerificationRequest.getEmail();
        String verificationPurpose = emailVerificationRequest.getPurpose();
        String verificationCode = codeGenerator.generate();

        if (verificationPolicyManager.isMailBlocked(email)) {
            return EmailResponse.EMAIL_AUTH_REQUEST_BLOCKED;
        }

        if (verificationPolicyManager.isExceedMailCodeResendAttempts(email)) {
            verificationPolicyManager.deleteAllPolicy(email);
            verificationPolicyManager.setMailBlock(email);
            return EmailResponse.TOO_MANY_CODE_RESEND_REQUESTS;
        }
        verificationPolicyManager.increaseAuthCodeResendAttempts(email);

        verificationService.saveCode(email, verificationPurpose, verificationCode);
        mailService.sendEmail(email, verificationPurpose, verificationCode);

        return EmailResponse.VERIFICATION_CODE_SENT;
    }


    @Transactional
    public Response verifyEmailCode(VerifyEmailCodeRequest verifyEmailCodeRequest) {
        String email = verifyEmailCodeRequest.getEmail();
        String verificationPurpose = verifyEmailCodeRequest.getPurpose();
        String verificationCode = verifyEmailCodeRequest.getVerificationCode();

        if (verificationPolicyManager.isMailBlocked(email)) {
            return EmailResponse.EMAIL_AUTH_REQUEST_BLOCKED;
        }

        if (verificationPolicyManager.isCodeIssued(email, EmailVerificationType.from(verificationPurpose))) {
            return EmailResponse.VERIFICATION_CODE_REQUIRED;
        }

        if (verificationPolicyManager.isExceedMailVerificationAttempts(email)) {
            verificationPolicyManager.deleteAllPolicy(email);
            verificationPolicyManager.setMailBlock(email);
            return EmailResponse.TOO_MANY_VERIFICATION_REQUESTS;
        }
        verificationPolicyManager.increaseMailVerificationAttempts(email);

        if (!verificationService.verifyCode(email, verificationPurpose, verificationCode)) {
            return EmailResponse.INVALID_VERIFICATION_CODE;
        }

        return EmailResponse.EMAIL_VERIFICATION_SUCCESS;
    }

    public EmailPolicyResponse getEmailVerificationAttempts(String mail) {
        int verificationAttempts = verificationPolicyManager.getMailVerificationAttempts(mail);
        int remainingVerificationAttempts =
                Integer.parseInt(EmailVerificationType.AUTH_ATTEMPT.getMaxStandard()) - verificationAttempts;
        String notificationMessage = String.format(
                EmailResponseMessage.VERIFICATION_ATTEMPTS_MESSAGE_TEMPLATE,
                remainingVerificationAttempts);

        return new EmailPolicyResponse(notificationMessage);
    }

    public EmailPolicyResponse getResendCodeAttempts(String mail) {
        int verificationAttempts = verificationPolicyManager.getMailCodeResendAttempts(mail);
        int remainingVerificationAttempts =
                Integer.parseInt(EmailVerificationType.CODE_RESEND.getMaxStandard()) - verificationAttempts;
        String notificationMessage = String.format(
                EmailResponseMessage.RESEND_CODE_ATTEMPTS_MESSAGE_TEMPLATE,
                remainingVerificationAttempts);

        return new EmailPolicyResponse(notificationMessage);
    }

    public EmailPolicyResponse getBlockExpired(String mail) {
        long expiredTime = verificationPolicyManager.getMailBlockExpiredTime(mail);
        String notificationMessage = String.format(EmailResponseMessage.BLOCK_RETRY_MESSAGE_TEMPLATE, expiredTime);
        return new EmailPolicyResponse(notificationMessage);
    }
}
