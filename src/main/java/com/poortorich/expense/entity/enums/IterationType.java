package com.poortorich.expense.entity.enums;

import com.poortorich.accountbook.response.AccountBookResponse;
import com.poortorich.global.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
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
        if (type == null || type.trim().isEmpty()) {
            return DEFAULT;
        }

        return Arrays.stream(IterationType.values())
                .filter(iteration -> Objects.equals(iteration.type, type))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(AccountBookResponse.ITERATION_TYPE_INVALID));
    }

    @Override
    public String toString() {
        return type;
    }
}
