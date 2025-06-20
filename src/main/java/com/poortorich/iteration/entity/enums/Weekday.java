package com.poortorich.iteration.entity.enums;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.response.IterationResponse;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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

        return Arrays.stream(Weekday.values())
                .filter(weekday -> Objects.equals(weekday.type, type))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(IterationResponse.DAY_OF_WEEK_INVALID));
    }

    public static List<Weekday> sortWeekday(List<Weekday> weekdays) {
        return weekdays.stream()
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .toList();
    }


    public static Weekday fromDayOfWeek(DayOfWeek dayOfWeek) {
        return Weekday.values()[dayOfWeek.getValue() % 7];
    }

    @Override
    public String toString() {
        return type;
    }
}
