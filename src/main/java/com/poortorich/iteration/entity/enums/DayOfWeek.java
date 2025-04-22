package com.poortorich.iteration.entity.enums;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.response.IterationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.util.Objects;

@RequiredArgsConstructor
public enum DayOfWeek {
    MONDAY("월"),
    TUESDAY("화"),
    WEDNESDAY("수"),
    THURSDAY("목"),
    FRIDAY("금"),
    SATURDAY("토"),
    SUNDAY("일");

    private final String type;

    public static DayOfWeek from(String type) {
        if (type == null) {
            return null;
        }

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (Objects.equals(dayOfWeek.type, type)) {
                return dayOfWeek;
            }
        }

        throw new BadRequestException(IterationResponse.DAY_OF_WEEK_INVALID);
    }
}
