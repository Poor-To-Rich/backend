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

    public void increaseMailVerificationAttempts(String mail) {
        this.increaseMailAuthAttempts(mail, EmailVerificationType.AUTH_ATTEMPT);
    }

    public void increaseAuthCodeResendAttempts(String mail) {
        this.increaseMailAuthAttempts(mail, EmailVerificationType.CODE_RESEND);
    }

    public boolean isMailBlocked(String mail) {
        return Boolean.parseBoolean(getPolicy(mail, EmailVerificationType.AUTH_BLOCK));
    }

    public int getMailVerificationAttempts(String mail) {
        return Integer.parseInt(getPolicy(mail, EmailVerificationType.AUTH_ATTEMPT));
    }

    public int getMailCodeResendAttempts(String mail) {
        return Integer.parseInt(getPolicy(mail, EmailVerificationType.CODE_RESEND));
    }

    public long getMailBlockExpiredTime(String mail) {
        return redisTemplate.getExpire(
                EmailVerificationType.AUTH_BLOCK.getRedisKey(mail),
                EmailVerificationType.AUTH_BLOCK.getTimeUnit());
    }

    public boolean isVerifiedMail(String mail) {
        return Boolean.parseBoolean(getPolicy(mail, EmailVerificationType.EMAIL_VERIFICATION));
    }

    public boolean isExceedMailVerificationAttempts(String mail) {
        return isExceedMailAuthAttempts(mail, EmailVerificationType.AUTH_ATTEMPT);
    }

    public boolean isExceedMailCodeResendAttempts(String mail) {
        return isExceedMailAuthAttempts(mail, EmailVerificationType.CODE_RESEND);
    }

    public boolean isCodeIssued(String mail, EmailVerificationType type) {
        return (type == EmailVerificationType.CHANGE_EMAIL
                || type == EmailVerificationType.EMAIL_VERIFICATION)
                && getPolicy(mail, type) != null;
    }

    public void deleteAllPolicy(String mail) {
        deletePolicy(mail, EmailVerificationType.AUTH_BLOCK);
        deletePolicy(mail, EmailVerificationType.AUTH_ATTEMPT);
        deletePolicy(mail, EmailVerificationType.CODE_RESEND);
        deletePolicy(mail, EmailVerificationType.EMAIL_VERIFICATION);
    }

    public void setMailBlock(String mail) {
        setPolicy(mail, EmailVerificationType.AUTH_BLOCK.getMaxStandard(), EmailVerificationType.AUTH_BLOCK);
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

    private void increaseMailAuthAttempts(String mail, EmailVerificationType verificationType) {
        String verificationAttempts = getPolicy(mail, verificationType);

        int increasedAttempts = Integer.parseInt(verificationAttempts);
        setPolicy(mail, String.valueOf(++increasedAttempts), verificationType);
    }

    private boolean isExceedMailAuthAttempts(String mail, EmailVerificationType verificationType) {
        return Objects.equals(verificationType.getMaxStandard(), getPolicy(mail, verificationType));
    }

}
