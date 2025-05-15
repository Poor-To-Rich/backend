package com.poortorich.global.date.util;

import com.poortorich.global.date.constants.DateConstants;
import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.domain.WeekInformation;
import com.poortorich.global.date.domain.YearInformation;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DateInfoProvider {

    public static YearInformation getYearInformation(Year year) {
        Map<Month, MonthInformation> months = new EnumMap<>(Month.class);
        Stream.of(Month.values())
                .forEach(month -> months.put(month, getMonthInformation(year.atMonth(month))));

        return YearInformation.builder()
                .year(year)
                .startDate(year.atDay(DateConstants.FIRST_DAY_OF_YEAR))
                .endDate(year.atMonth(Month.DECEMBER).atEndOfMonth())
                .months(months)
                .build();
    }

    public static MonthInformation getMonthInformation(YearMonth yearMonth) {
        return MonthInformation.builder()
                .yearMonth(yearMonth)
                .startDate(yearMonth.atDay(DateConstants.FIRST_DAY_OF_MONTH))
                .endDate(yearMonth.atEndOfMonth())
                .weeks(DateInfoProvider.getWeeksInformation(yearMonth))
                .build();
    }

    public static List<WeekInformation> getWeeksInformation(YearMonth yearMonth) {
        List<WeekInformation> weeks = new ArrayList<>();
        LocalDate weekStart = yearMonth.atDay(DateConstants.FIRST_DAY_OF_MONTH)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        while (!weekStart.isAfter(yearMonth.atEndOfMonth())) {
            LocalDate weekEnd = weekStart.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
            weeks.add(WeekInformation.builder()
                    .startDate(weekStart)
                    .endDate(weekEnd)
                    .build());
            weekStart = weekEnd.plusDays(DateConstants.ONE_DAY);
        }

        return weeks;
    }
}
