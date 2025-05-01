package com.poortorich.email.util;

import com.poortorich.email.enums.EmailVerificationType;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;
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

    public void increaseEmailVerificationAttempts(String mail) {
        this.increaseEmailVerificationAttemptsByType(mail, EmailVerificationType.AUTH_ATTEMPT);
    }

    public void increaseAuthCodeResendAttempts(String mail) {
        this.increaseEmailVerificationAttemptsByType(mail, EmailVerificationType.CODE_RESEND);
    }

    public void blockEmail(String mail) {
        setPolicy(mail, EmailVerificationType.AUTH_BLOCK.getMaxStandard(), EmailVerificationType.AUTH_BLOCK);
    }

    public void deleteAllPolicy(String mail) {
        deletePolicy(mail, EmailVerificationType.AUTH_BLOCK);
        deletePolicy(mail, EmailVerificationType.AUTH_ATTEMPT);
        deletePolicy(mail, EmailVerificationType.CODE_RESEND);
        deletePolicy(mail, EmailVerificationType.EMAIL_VERIFICATION);
    }

    public boolean isEmailBlocked(String mail) {
        return Boolean.parseBoolean(getPolicy(mail, EmailVerificationType.AUTH_BLOCK));
    }

    public boolean isEmailVerified(String mail) {
        return Boolean.parseBoolean(getPolicy(mail, EmailVerificationType.EMAIL_VERIFICATION));
    }

    public boolean isVerificationCodeIssued(String mail, EmailVerificationType type) {
        return (type == EmailVerificationType.CHANGE_EMAIL
                || type == EmailVerificationType.EMAIL_VERIFICATION)
                && getPolicy(mail, type) != null;
    }

    public boolean hasExceededEmailVerificationAttempts(String mail) {
        return hasExceededEmailAttemptsByType(mail, EmailVerificationType.AUTH_ATTEMPT);
    }

    public boolean hasExceededAuthCodeResendAttempts(String mail) {
        return hasExceededEmailAttemptsByType(mail, EmailVerificationType.CODE_RESEND);
    }

    public int getEmailVerificationAttempts(String mail) {
        return Integer.parseInt(getPolicy(mail, EmailVerificationType.AUTH_ATTEMPT));
    }

    public int getAuthCodeResendAttempts(String mail) {
        return Integer.parseInt(getPolicy(mail, EmailVerificationType.CODE_RESEND));
    }

    public long getMailBlockExpirationTime(String mail) {
        return redisTemplate.getExpire(
                EmailVerificationType.AUTH_BLOCK.getRedisKey(mail),
                EmailVerificationType.AUTH_BLOCK.getTimeUnit());
    }

    private void setPolicy(String mail, String value, EmailVerificationType type) {
        valueOps.set(
                type.getRedisKey(mail),
                value,
                type.getTimeToLive(),
                type.getTimeUnit()
        );
    }

    private String getPolicy(String mail, EmailVerificationType type) {
        return Optional.ofNullable(valueOps.get(type.getRedisKey(mail))).orElse(type.getMinStandard());
    }

    private void deletePolicy(String mail, EmailVerificationType type) {
        valueOps.getOperations().delete(type.getRedisKey(mail));
    }

    private void increaseEmailVerificationAttemptsByType(String mail, EmailVerificationType verificationType) {
        String verificationAttempts = getPolicy(mail, verificationType);

        int increasedAttempts = Integer.parseInt(verificationAttempts);
        setPolicy(mail, String.valueOf(++increasedAttempts), verificationType);
    }

    private boolean hasExceededEmailAttemptsByType(String mail, EmailVerificationType verificationType) {
        return Objects.equals(verificationType.getMaxStandard(), getPolicy(mail, verificationType));
    }

}
