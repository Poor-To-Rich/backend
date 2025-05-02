package com.poortorich.email.validator.annotations;

import com.poortorich.email.constants.EmailResponseMessage;
import com.poortorich.email.validator.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface Email {

    String message() default EmailResponseMessage.EMAIL_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
