package com.poortorich.iteration.entity.enums;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.response.IterationResponse;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public enum IterationRuleType {

    DAILY("daily", 3650),
    WEEKLY("weekly", 520),
    MONTHLY("monthly", 120),
    YEARLY("yearly", 10);

    public final String type;
    public final int maxIterations;

    public static IterationRuleType from(String type) {
        for (IterationRuleType iteration : IterationRuleType.values()) {
            if (Objects.equals(iteration.type, type)) {
                return iteration;
            }
        }

        throw new BadRequestException(IterationResponse.ITERATION_RULE_TYPE_INVALID);
    }
}
