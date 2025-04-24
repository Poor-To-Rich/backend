package com.poortorich.iteration.validator;

import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.constants.IterationValidationConstraints;
import com.poortorich.iteration.entity.enums.IterationRuleType;
import com.poortorich.iteration.request.IterationRule;
import com.poortorich.iteration.request.MonthlyOption;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class IterationRuleValidator implements ConstraintValidator<IterationRuleCheck, IterationRule> {

    @Override
    public boolean isValid(IterationRule rule, ConstraintValidatorContext context) {
        IterationRuleType type = rule.parseIterationType();

        boolean daysOfWeekValid = validateDaysOfWeek(type, rule.getDaysOfWeek(), context);
        boolean monthlyOptionValid = validateMonthlyOption(type, rule.getMonthlyOption(), context);

        return daysOfWeekValid && monthlyOptionValid;
    }

    private boolean validateDaysOfWeek(IterationRuleType type, List<String> daysOfWeek, ConstraintValidatorContext context) {
        if (type == IterationRuleType.WEEKLY && (daysOfWeek == null || daysOfWeek.isEmpty())) {
            buildCustomMessage(
                    context,
                    IterationResponseMessages.DAY_OF_WEEK_REQUIRED_WEEKLY_TYPE,
                    IterationValidationConstraints.DAYS_OF_WEEK_FIELD_NAME
            );
            return false;
        }

        return true;
    }

    private boolean validateMonthlyOption(IterationRuleType type, MonthlyOption monthlyOption, ConstraintValidatorContext context) {
        if (type == IterationRuleType.MONTHLY && monthlyOption == null) {
            buildCustomMessage(
                    context,
                    IterationResponseMessages.MONTHLY_OPTION_REQUIRED_MONTHLY_TYPE,
                    IterationValidationConstraints.MONTHLY_OPTION_FIELD_NAME
            );
            return false;
        }

        return true;
    }

    private void buildCustomMessage(ConstraintValidatorContext context, String message, String fieldName) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(fieldName)
                .addConstraintViolation();
    }
}
