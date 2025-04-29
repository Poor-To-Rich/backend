package com.poortorich.iteration.entity.enums;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.response.IterationResponse;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.util.Objects;

@RequiredArgsConstructor
public enum Weekday {

    SUNDAY("일"),
    MONDAY("월"),
    TUESDAY("화"),
    WEDNESDAY("수"),
    THURSDAY("목"),
    FRIDAY("금"),
    SATURDAY("토");

    private final String type;

    public static Weekday from(String type) {
        if (type == null) {
            return null;
        }

        for (Weekday weekday : Weekday.values()) {
            if (Objects.equals(weekday.type, type)) {
                return weekday;
            }
        }

        throw new BadRequestException(IterationResponse.DAY_OF_WEEK_INVALID);
    }

    public DayOfWeek toDayOfWeek() {
        if (this.ordinal() == 0) {
            return DayOfWeek.of(7);
        }

        return DayOfWeek.of(this.ordinal());
    }

    public static Weekday fromDayOfWeek(DayOfWeek dayOfWeek) {
        return Weekday.values()[dayOfWeek.getValue() % 7];
    }
}
