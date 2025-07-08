package com.poortorich.iteration.validator;

import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.constants.IterationValidationConstraints;
import com.poortorich.iteration.request.MonthlyOption;
import com.poortorich.iteration.validator.annotations.MonthlyOptionCheck;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MonthlyOptionValidator implements ConstraintValidator<MonthlyOptionCheck, MonthlyOption> {

    @Override
    public boolean isValid(MonthlyOption monthlyOption, ConstraintValidatorContext context) {
        boolean dayValid = validateNotNull(monthlyOption.getDay(), context,
                IterationResponseMessages.MONTHLY_OPTION_DAY_REQUIRED,
                IterationValidationConstraints.MONTHLY_OPTION_DAY_FIELD_NAME);

        boolean weekValid = validateNotNull(monthlyOption.getWeek(), context,
                IterationResponseMessages.MONTHLY_OPTION_WEEK_REQUIRED,
                IterationValidationConstraints.MONTHLY_OPTION_WEEK_FIELD_NAME);

        boolean dayOfWeekValid = validateNotNull(monthlyOption.getDayOfWeek(), context,
                IterationResponseMessages.MONTHLY_OPTION_DAY_OF_WEEK_REQUIRED,
                IterationValidationConstraints.MONTHLY_OPTION_DAY_OF_WEEK_FIELD_NAME);

        return dayValid && weekValid && dayOfWeekValid;
    }

    private boolean validateNotNull(Object value, ConstraintValidatorContext context, String message, String fieldName) {
        if (value == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(fieldName)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
