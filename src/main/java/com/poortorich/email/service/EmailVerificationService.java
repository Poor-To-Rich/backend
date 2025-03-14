package com.poortorich.email.service;

import com.poortorich.email.enums.EmailVerificationType;
import com.poortorich.email.util.EmailVerificationPolicyManager;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOps;

    private final EmailVerificationPolicyManager verificationPolicyManager;

    @PostConstruct
    public void init() {
        valueOps = redisTemplate.opsForValue();
    }

    public void saveCode(String mail, String purpose, String code) {
        EmailVerificationType type = EmailVerificationType.getTypeByPurpose(purpose);

        verificationPolicyManager.checkBeforeCodeSaved(mail);

        valueOps.set(
                type.getRedisKey(mail),
                code,
                type.getTimeToLive(),
                type.getTimeUnit()
        );
    }

    public boolean verifyCode(String mail, String purpose, String code) {
        EmailVerificationType type = EmailVerificationType.getTypeByPurpose(purpose);

        verificationPolicyManager.checkBeforeVerified(mail, purpose);

        boolean isVerified = Objects.equals(getCode(mail, type), code);
        if (isVerified) {
            verificationPolicyManager.checkAfterVerifiedSuccess(mail, purpose);
            setVerifiedEmail(mail);
        }

        return isVerified;
    }

    private void setVerifiedEmail(String mail) {
        valueOps.set(
                EmailVerificationType.EMAIL_VERIFICATION.getRedisKey(mail),
                EmailVerificationType.EMAIL_VERIFICATION.getMaxStandard(),
                EmailVerificationType.EMAIL_VERIFICATION.getTimeToLive(),
                EmailVerificationType.EMAIL_VERIFICATION.getTimeUnit()
        );
    }

    private String getCode(String mail, EmailVerificationType type) {
        return valueOps.get(type.getRedisKey(mail));
    }

}
