package com.poortorich.email.util;

import com.poortorich.email.enums.EmailVerificationType;
import com.poortorich.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailChecker {

    private final UserValidator userValidator;

    public void checkByVerificationType(String email, EmailVerificationType type) {
        switch (type) {
            case FIND_USERNAME, CHANGE_PASSWORD -> userValidator.validateEmailExists(email);
            default -> userValidator.validateEmailDuplicate(email);
        }
    }

    public void checkByVerificationType(String email, String purpose) {
        checkByVerificationType(email, EmailVerificationType.from(purpose));
    }
}
