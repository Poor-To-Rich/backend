package com.poortorich.expense.request.enums;

import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.global.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum IterationAction {

    NONE("NONE"),
    THIS_ONLY("THIS_ONLY"),
    THIS_AND_FUTURE("THIS_AND_FUTURE"),
    ALL("ALL");

    private final String action;

    public static IterationAction from(String action) {
        if (Objects.isNull(action)) {
            return NONE;
        }

        return Arrays.stream(IterationAction.values())
                .filter(iterationAction -> Objects.equals(iterationAction.action, action))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ExpenseResponse.ITERATION_ACTION_INVALID));
    }
}
