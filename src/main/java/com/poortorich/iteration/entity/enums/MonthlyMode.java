package com.poortorich.iteration.entity.enums;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.response.IterationResponse;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum MonthlyMode {

    DAY("dayOfMonth"),
    WEEKDAY("weekdayOfMonth"),
    END("endOfMonth");

    private final String mode;

    public static MonthlyMode from(String mode) {
        return Arrays.stream(MonthlyMode.values())
                .filter(monthly -> Objects.equals(monthly.mode, mode))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(IterationResponse.MONTHLY_MODE_INVALID, "monthlyOption.mode"));
    }
}
