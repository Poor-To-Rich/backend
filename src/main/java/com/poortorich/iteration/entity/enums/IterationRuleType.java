package com.poortorich.iteration.entity.enums;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.response.enums.IterationResponse;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum IterationRuleType {

    DAILY("daily", 50000),
    WEEKLY("weekly", 50000),
    MONTHLY("monthly", 50000),
    YEARLY("yearly", 50000);

    private final String type;
    public final int maxIterations;

    public static IterationRuleType from(String type) {
        return Arrays.stream(IterationRuleType.values())
                .filter(iterationRule -> Objects.equals(iterationRule.type, type))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(IterationResponse.ITERATION_RULE_TYPE_INVALID));
    }

    @Override
    public String toString() {
        return type;
    }
}
