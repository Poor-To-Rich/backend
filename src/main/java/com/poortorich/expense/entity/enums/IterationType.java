package com.poortorich.expense.entity.enums;

import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.global.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public enum IterationType {

    DEFAULT("반복없음"),
    EVERY_DAY("매일"),
    EVERY_DAY_OF_THE_WEEK("주중 매일"),
    EVERY_WEEK("매주"),
    EVERY_SECOND_WEEK("2주마다"),
    EVERY_MONTH("매달"),
    LAST_DAY_OF_EVERY_MONTH("매달 말일"),
    EVERY_YEAR("매년");

    public final String type;

    public static IterationType from(String type) {
        if (type == null || type.trim().isEmpty()) {
            return DEFAULT;
        }

        for (IterationType iteration : IterationType.values()) {
            if (Objects.equals(iteration.type, type)) {
                return iteration;
            }
        }

        throw new BadRequestException(ExpenseResponse.ITERATION_TYPE_INVALID);
    }
}
