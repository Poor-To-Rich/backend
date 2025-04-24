package com.poortorich.email.validator;

import com.poortorich.email.constants.EmailValidationConstraints;
import com.poortorich.email.validator.annotations.Email;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null || email.isEmpty()) {
            return true;
        }

        return EmailValidationConstraints.EMAIL_PATTERN.matcher(email).matches();
    }
}
