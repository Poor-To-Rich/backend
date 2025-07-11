package com.poortorich.email.facade;

import com.poortorich.email.constants.EmailResponseMessage;
import com.poortorich.email.enums.EmailVerificationType;
import com.poortorich.email.request.EmailVerificationRequest;
import com.poortorich.email.request.VerifyEmailCodeRequest;
import com.poortorich.email.response.EmailPolicyResponse;
import com.poortorich.email.response.enums.EmailResponse;
import com.poortorich.email.service.EmailVerificationService;
import com.poortorich.email.service.MailService;
import com.poortorich.email.util.EmailVerificationPolicyManager;
import com.poortorich.email.util.UserMailChecker;
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

    private final UserMailChecker userMailChecker;

    @Transactional
    public Response sendVerificationCode(EmailVerificationRequest emailVerificationRequest) {
        String email = emailVerificationRequest.getEmail();

        String verificationPurpose = emailVerificationRequest.getPurpose();
        String verificationCode = codeGenerator.generate();

        userMailChecker.checkByVerificationType(email, verificationPurpose);

        if (verificationPolicyManager.isEmailBlocked(email)) {
            return EmailResponse.EMAIL_AUTH_REQUEST_BLOCKED;
        }

        if (verificationPolicyManager.hasExceededAuthCodeResendAttempts(email)) {
            verificationPolicyManager.deleteAllPolicy(email);
            verificationPolicyManager.blockEmail(email);
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

        if (verificationPolicyManager.isEmailBlocked(email)) {
            return EmailResponse.EMAIL_AUTH_REQUEST_BLOCKED;
        }

        if (!verificationPolicyManager.isVerificationCodeIssued(email,
                EmailVerificationType.from(verificationPurpose))) {
            return EmailResponse.VERIFICATION_CODE_REQUIRED;
        }

        if (verificationPolicyManager.hasExceededEmailVerificationAttempts(email)) {
            verificationPolicyManager.deleteAllPolicy(email);
            verificationPolicyManager.blockEmail(email);
            return EmailResponse.TOO_MANY_VERIFICATION_REQUESTS;
        }
        verificationPolicyManager.increaseEmailVerificationAttempts(email);

        if (!verificationService.verifyCode(email, verificationPurpose, verificationCode)) {
            return EmailResponse.VERIFICATION_CODE_INVALID;
        }

        return EmailResponse.EMAIL_VERIFICATION_SUCCESS;
    }

    public EmailPolicyResponse getEmailVerificationAttempts(String mail) {
        int verificationAttempts = verificationPolicyManager.getEmailVerificationAttempts(mail);
        int remainingVerificationAttempts =
                Integer.parseInt(EmailVerificationType.AUTH_ATTEMPT.getMaxStandard()) - verificationAttempts;
        String notificationMessage = String.format(
                EmailResponseMessage.VERIFICATION_ATTEMPTS_MESSAGE_TEMPLATE,
                remainingVerificationAttempts);

        return new EmailPolicyResponse(notificationMessage);
    }

    public EmailPolicyResponse getResendCodeAttempts(String mail) {
        int verificationAttempts = verificationPolicyManager.getAuthCodeResendAttempts(mail);
        int remainingVerificationAttempts =
                Integer.parseInt(EmailVerificationType.CODE_RESEND.getMaxStandard()) - verificationAttempts;
        String notificationMessage = String.format(
                EmailResponseMessage.RESEND_CODE_ATTEMPTS_MESSAGE_TEMPLATE,
                remainingVerificationAttempts);

        return new EmailPolicyResponse(notificationMessage);
    }

    public EmailPolicyResponse getBlockExpired(String mail) {
        long expiredTime = verificationPolicyManager.getMailBlockExpirationTime(mail);
        String notificationMessage = String.format(EmailResponseMessage.BLOCK_RETRY_MESSAGE_TEMPLATE, expiredTime);
        return new EmailPolicyResponse(notificationMessage);
    }
}
