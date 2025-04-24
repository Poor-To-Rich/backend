package com.poortorich.iteration.validator;

import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.constants.IterationValidationConstraints;
import com.poortorich.iteration.entity.enums.EndType;
import com.poortorich.iteration.request.End;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class EndValidator implements ConstraintValidator<EndCheck, End> {

    @Override
    public boolean isValid(End end, ConstraintValidatorContext context) {
        EndType type = end.parseEndType();

        boolean countValid = countValid(type, end.getCount(), context);
        boolean dateValid = dateValid(type, end.getDate(), context);

        return countValid && dateValid;
    }

    private boolean countValid(EndType type, Integer count, ConstraintValidatorContext context) {
        if (type == EndType.AFTER && count == null) {
            buildCustomMessage(
                    context,
                    IterationResponseMessages.END_COUNT_REQUIRED,
                    IterationValidationConstraints.END_COUNT_FIELD_NAME
            );
            return false;
        }

        return true;
    }

    private boolean dateValid(EndType type, LocalDate date, ConstraintValidatorContext context) {
        if (type == EndType.UNTIL && date == null) {
            buildCustomMessage(
                    context,
                    IterationResponseMessages.END_DATE_REQUIRED,
                    IterationValidationConstraints.END_DATE_FIELD_NAME
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
