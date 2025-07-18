package com.poortorich.email.enums;

import com.poortorich.email.response.enums.EmailResponse;
import com.poortorich.global.exceptions.BadRequestException;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailVerificationType {

    REGISTER("register", null, null, 10, TimeUnit.MINUTES),
    CHANGE_EMAIL("changeEmail", null, null, 10, TimeUnit.MINUTES),
    FIND_USERNAME("findUsername", null, null, 10, TimeUnit.MINUTES),
    CHANGE_PASSWORD("changePassword", null, null, 10, TimeUnit.MINUTES),
    AUTH_ATTEMPT("attempt", "0", "5", 30, TimeUnit.MINUTES),
    CODE_RESEND("resend", "0", "3", 30, TimeUnit.MINUTES),
    AUTH_BLOCK("block", "false", "true", 30, TimeUnit.MINUTES),
    EMAIL_VERIFICATION("verified", "false", "true", 1, TimeUnit.DAYS);

    private static final String REDIS_KEY_PATTERN = "%s:%s";

    private final String purpose;
    private final String minStandard;
    private final String maxStandard;
    private final Integer timeToLive;
    private final TimeUnit timeUnit;

    public static EmailVerificationType from(String purpose) {
        for (EmailVerificationType type : EmailVerificationType.values()) {
            if (type.purpose.equals(purpose)) {
                return type;
            }
        }
        throw new BadRequestException(EmailResponse.PURPOSE_INVALID);
    }

    public String getRedisKey(String mail) {
        return String.format(REDIS_KEY_PATTERN, mail, this.purpose);
    }
}
