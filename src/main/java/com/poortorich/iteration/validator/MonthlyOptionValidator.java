package com.poortorich.iteration.validator;

import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.constants.IterationValidationConstraints;
import com.poortorich.iteration.entity.enums.MonthlyMode;
import com.poortorich.iteration.request.MonthlyOption;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MonthlyOptionValidator implements ConstraintValidator<MonthlyOptionCheck, MonthlyOption> {

    @Override
    public boolean isValid(MonthlyOption monthlyOption, ConstraintValidatorContext context) {
        MonthlyMode mode = monthlyOption.parseMonthlyMode();

        boolean dayOfMonthValid = dayOfMonthValid(mode, monthlyOption.getDay(), context);
        boolean weekdayOfMonthValid = weekdayOfMonthValid(mode, monthlyOption.getWeek(), monthlyOption.getDayOfWeek(), context);

        return dayOfMonthValid && weekdayOfMonthValid;
    }

    private boolean dayOfMonthValid(MonthlyMode mode, Integer day, ConstraintValidatorContext context) {
        if (mode == MonthlyMode.DAY && day == null) {
            buildCustomMessage(
                    context,
                    IterationResponseMessages.MONTHLY_OPTION_DAY_INVALID,
                    IterationValidationConstraints.MONTHLY_OPTION_DAY_FIELD_NAME
            );
            return false;
        }

        return true;
    }

    private boolean weekdayOfMonthValid(MonthlyMode mode, Integer week, String dayOfWeek, ConstraintValidatorContext context) {
        if (mode == MonthlyMode.WEEKDAY) {
            boolean weekValid = weekValid(week, context);
            boolean dayOfWeekValid = dayOfWeekValid(dayOfWeek, context);

            return weekValid && dayOfWeekValid;
        }

        return true;
    }

    private boolean weekValid(Integer week, ConstraintValidatorContext context) {
        if (week == null) {
            buildCustomMessage(
                    context,
                    IterationResponseMessages.MONTHLY_OPTION_WEEK_INVALID,
                    IterationValidationConstraints.MONTHLY_OPTION_WEEK_FIELD_NAME
            );
            return false;
        }

        return true;
    }

    private boolean dayOfWeekValid(String dayOfWeek, ConstraintValidatorContext context) {
        if (dayOfWeek == null) {
            buildCustomMessage(
                    context,
                    IterationResponseMessages.MONTHLY_OPTION_DAY_OF_WEEK_INVALID,
                    IterationValidationConstraints.MONTHLY_OPTION_DAY_OF_WEEK_FIELD_NAME
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
