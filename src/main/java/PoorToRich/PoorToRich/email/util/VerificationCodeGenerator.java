package PoorToRich.PoorToRich.email.util;

import java.util.Random;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class VerificationCodeGenerator extends Random {

    private static final Integer BEGIN = 100000;

    private static final Integer END = 1000000;

    public String generate() {
        int bound = VerificationCodeGenerator.END - VerificationCodeGenerator.BEGIN;
        Integer verificationCode = this.nextInt(bound) + VerificationCodeGenerator.BEGIN;
        return String.valueOf(verificationCode);
    }
}
