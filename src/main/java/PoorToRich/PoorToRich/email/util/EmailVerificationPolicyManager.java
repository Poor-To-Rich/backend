package PoorToRich.PoorToRich.email.util;

import PoorToRich.PoorToRich.email.enums.EmailResponse;
import PoorToRich.PoorToRich.email.enums.EmailVerificationType;
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
                EmailVerificationType.EMAIL_VERIFICATION.getMinState()
        );
    }

    public void checkBeforeVerified(String mail, String purpose) {
        checkBlockPolicy(mail);
        checkCodeExpiration(mail, purpose);
        checkFirstRequest(mail, EmailVerificationType.AUTH_ATTEMPT);
        checkExceedVerifiedCode(mail);
        increaseAttempts(mail, EmailVerificationType.AUTH_ATTEMPT);
    }


    private void checkFirstRequest(String mail, EmailVerificationType type) {
        if (valueOps.get(type.getKey(mail)) == null) {
            setPolicy(mail, type, type.getMinState());
        }
    }

    private void checkCodeExpiration(String mail, String purpose) {
        String isVerified = valueOps.get(EmailVerificationType.EMAIL_VERIFICATION.getKey(mail));
        String verificationCode = valueOps.get(EmailVerificationType.getByKey(purpose).getKey(mail));

        if (isVerified != null && !Boolean.parseBoolean(isVerified) && verificationCode == null) {
            throw new UnauthorizedException(EmailResponse.VERIFICATION_CODE_EXPIRED);
        }
    }

    private void checkExceedVerifiedCode(String mail) {
        String authAttempts = valueOps.get(EmailVerificationType.AUTH_ATTEMPT.getKey(mail));

        if (EmailVerificationType.AUTH_ATTEMPT.getMaxState().equals(authAttempts)) {
            deleteAllPolicy(mail);
            setBlockPolicy(mail);
            throw new TooManyRequestException(EmailResponse.TOO_MANY_VERIFICATION_REQUESTS);
        }
    }

    private void checkExceedCodeResend(String mail) {
        String codeResendAttempts = valueOps.get(EmailVerificationType.CODE_RESEND.getKey(mail));
        if (EmailVerificationType.CODE_RESEND.getMaxState().equals(codeResendAttempts)) {
            deleteAllPolicy(mail);
            setBlockPolicy(mail);
            throw new TooManyRequestException(EmailResponse.TOO_MANY_CODE_RESEND_REQUESTS);
        }
    }

    private void checkBlockPolicy(String mail) {
        if (Boolean.parseBoolean(valueOps.get(EmailVerificationType.AUTH_BLOCK.getKey(mail)))) {
            throw new TooManyRequestException(EmailResponse.TOO_MANY_REQUEST_MAIL);
        }
    }

    private void increaseAttempts(String mail, EmailVerificationType type) {
        String attempts = valueOps.get(type.getKey(mail));

        if (attempts != null) {
            int newAttempts = Integer.parseInt(attempts);
            setPolicy(mail, type, String.valueOf(++newAttempts));
        }
    }

    private void setBlockPolicy(String mail) {
        setPolicy(mail, EmailVerificationType.AUTH_BLOCK, EmailVerificationType.AUTH_BLOCK.getMaxState());
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
    }

    private void deletePolicy(String mail, EmailVerificationType type) {
        valueOps.getOperations().delete(type.getKey(mail));
    }
}
