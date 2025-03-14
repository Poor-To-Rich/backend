package com.poortorich.email.util;

import java.security.SecureRandom;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class VerificationCodeGenerator {

    private static final int CODE_RANGE_MIN = 100000;
    private static final int CODE_RANGE_MAX = 1000000;
    private static final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        int bound = CODE_RANGE_MAX - CODE_RANGE_MIN;
        int verificationCode = secureRandom.nextInt(bound) + CODE_RANGE_MIN;
        return String.valueOf(verificationCode);
    }
}
