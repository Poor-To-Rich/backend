package PoorToRich.PoorToRich.email.util;

import PoorToRich.PoorToRich.email.enums.EmailResponse;
import PoorToRich.PoorToRich.email.enums.EmailVerificationType;
import PoorToRich.PoorToRich.global.exceptions.BadRequestException;
import PoorToRich.PoorToRich.global.exceptions.TooManyRequestException;
import PoorToRich.PoorToRich.global.exceptions.UnauthorizedException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationPolicyManager {

    private final StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOps;

    @PostConstruct
    public void init() {
        valueOps = redisTemplate.opsForValue();
    }

    public void checkBeforeCodeSaved(String mail) {
        checkBlockPolicy(mail);
        checkFirstRequest(mail, EmailVerificationType.CODE_RESEND);
        checkExceedCodeResend(mail);
        increaseAttempts(mail, EmailVerificationType.CODE_RESEND);
        setPolicy(
                mail,
                EmailVerificationType.EMAIL_VERIFICATION,
                EmailVerificationType.EMAIL_VERIFICATION.getMinStandard()
        );
    }

    public void checkBeforeVerified(String mail, String purpose) {
        checkBlockPolicy(mail);
        checkCodeIssued(mail);
        checkCodeExpiration(mail, purpose);
        checkFirstRequest(mail, EmailVerificationType.AUTH_ATTEMPT);
        checkExceedVerifiedCode(mail);
        increaseAttempts(mail, EmailVerificationType.AUTH_ATTEMPT);
    }

    public void checkAfterVerifiedSuccess(String mail, String purpose) {
        deletePolicy(mail, EmailVerificationType.getTypeByPurpose(purpose));
        deleteAllPolicy(mail);
    }

    private void checkBlockPolicy(String mail) {
        if (Boolean.parseBoolean(valueOps.get(EmailVerificationType.AUTH_BLOCK.getKey(mail)))) {
            throw new TooManyRequestException(EmailResponse.TOO_MANY_REQUEST_MAIL);
        }
    }

    private void checkFirstRequest(String mail, EmailVerificationType type) {
        if (valueOps.get(type.getKey(mail)) == null) {
            setPolicy(mail, type, type.getMinStandard());
        }
    }

    private void checkExceedCodeResend(String mail) {
        String codeResendAttempts = valueOps.get(EmailVerificationType.CODE_RESEND.getKey(mail));
        if (EmailVerificationType.CODE_RESEND.getMaxStandard().equals(codeResendAttempts)) {
            deleteAllPolicy(mail);
            setBlockPolicy(mail);
            throw new TooManyRequestException(EmailResponse.TOO_MANY_CODE_RESEND_REQUESTS);
        }
    }

    private void checkExceedVerifiedCode(String mail) {
        String authAttempts = valueOps.get(EmailVerificationType.AUTH_ATTEMPT.getKey(mail));

        if (EmailVerificationType.AUTH_ATTEMPT.getMaxStandard().equals(authAttempts)) {
            deleteAllPolicy(mail);
            setBlockPolicy(mail);
            throw new TooManyRequestException(EmailResponse.TOO_MANY_VERIFICATION_REQUESTS);
        }
    }

    private void increaseAttempts(String mail, EmailVerificationType type) {
        String attempts = valueOps.get(type.getKey(mail));

        if (attempts != null) {
            int newAttempts = Integer.parseInt(attempts);
            setPolicy(mail, type, String.valueOf(++newAttempts));
        }
    }

    private void checkCodeIssued(String mail) {
        String isVerified = valueOps.get(EmailVerificationType.EMAIL_VERIFICATION.getKey(mail));

        if (isVerified == null) {
            throw new BadRequestException(EmailResponse.VERIFICATION_CODE_REQUIRED);
        }
    }

    private void checkCodeExpiration(String mail, String purpose) {
        String verificationCode = valueOps.get(EmailVerificationType.getTypeByPurpose(purpose).getKey(mail));

        if (verificationCode == null) {
            throw new UnauthorizedException(EmailResponse.VERIFICATION_CODE_EXPIRED);
        }
    }

    private void setBlockPolicy(String mail) {
        setPolicy(mail, EmailVerificationType.AUTH_BLOCK, EmailVerificationType.AUTH_BLOCK.getMaxStandard());
    }

    private void setPolicy(String mail, EmailVerificationType type, String value) {
        valueOps.set(
                type.getKey(mail),
                value,
                type.getTimeToLive(),
                type.getTimeUnit()
        );
    }

    private void deleteAllPolicy(String mail) {
        deletePolicy(mail, EmailVerificationType.AUTH_BLOCK);
        deletePolicy(mail, EmailVerificationType.AUTH_ATTEMPT);
        deletePolicy(mail, EmailVerificationType.CODE_RESEND);
        deletePolicy(mail, EmailVerificationType.EMAIL_VERIFICATION);
    }

    private void deletePolicy(String mail, EmailVerificationType type) {
        valueOps.getOperations().delete(type.getKey(mail));
    }
}
