package com.poortorich.expense.entity.enums;

import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.global.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public enum IterationType {

    DEFAULT("none"),
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly"),
    WEEKDAY("weekday"),
    END_OF_MONTH("endOfMonth"),
    CUSTOM("custom");

    public final String type;

    public static IterationType from(String type) {
        for (IterationType iteration : IterationType.values()) {
            if (Objects.equals(iteration.type, type)) {
                return iteration;
            }
        }

        throw new BadRequestException(ExpenseResponse.ITERATION_TYPE_INVALID);
    }
}
