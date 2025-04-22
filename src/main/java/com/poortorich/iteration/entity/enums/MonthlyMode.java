package com.poortorich.iteration.entity.enums;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.response.IterationResponse;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public enum MonthlyMode {

    DAY("dayOfMonth"),
    WEEKDAY("weekdayOfMonth"),
    END("endOfMonth");

    private final String mode;

    public static MonthlyMode from(String mode) {
        for (MonthlyMode monthlyMode : MonthlyMode.values()) {
            if (Objects.equals(monthlyMode.mode, mode)) {
                return monthlyMode;
            }
        }

        throw new BadRequestException(IterationResponse.MONTHLY_MODE_INVALID);
    }
}
