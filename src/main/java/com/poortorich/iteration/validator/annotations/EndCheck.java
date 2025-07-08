package com.poortorich.iteration.validator.annotations;

import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.validator.EndValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EndValidator.class)
public @interface EndCheck {

    String message() default IterationResponseMessages.CHECK_DEFAULT_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
