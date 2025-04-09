package com.poortorich.global.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestValidTestHelper {

    public static <T> void assertValidationMessage(T request, String expectedErrorMessage) {
        Validator validator;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }

        Set<ConstraintViolation<T>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getMessage().equals(expectedErrorMessage));
    }
}
