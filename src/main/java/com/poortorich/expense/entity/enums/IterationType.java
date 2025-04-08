package com.poortorich.expense.entity.enums;

import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.global.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public enum IterationType {

    DEFAULT("반복 없음"),
    EVERY_DAY("매일"),
    EVERY_WEEK("매주"),
    EVERY_MONTH("매달"),
    EVERY_YEAR("매년"),
    EVERY_WEEKDAYS("주중 매일"),
    USER_FRIENDLY("사용자화");

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
