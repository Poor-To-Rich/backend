package com.poortorich.global.date.util;

import com.poortorich.global.date.constants.DateConstants;
import com.poortorich.global.date.constants.DatePattern;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.domain.WeekInformation;
import com.poortorich.global.date.domain.YearInformation;
import com.poortorich.global.date.response.enums.DateResponse;
import com.poortorich.global.exceptions.BadRequestException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DateInfoProvider {

    private static final Map<Predicate<String>, Function<String, DateInfo>> DATE_PARSER = new LinkedHashMap<>();
    private static final int PREVIOUS_PERIODS = 5;

    static {
        DATE_PARSER.put(
                Objects::isNull,
                date -> getMonthInformation(YearMonth.now())
        );

        DATE_PARSER.put(
                date -> date != null && date.matches(DatePattern.YEAR_REGEX),
                date -> getYearInformation(Year.parse(date))
        );

        DATE_PARSER.put(
                date -> date != null && date.matches(DatePattern.YEAR_MONTH_REGEX),
                date -> getMonthInformation(YearMonth.parse(date))
        );
    }

    public static DateInfo getDateInfo(String date) {
        return DATE_PARSER.entrySet().stream()
                .filter(entry -> entry.getKey().test(date))
                .findFirst()
                .map(entry -> entry.getValue().apply(date))
                .orElseThrow(() -> new BadRequestException(DateResponse.UNSUPPORTED_DATE_FORMAT));
    }

    public static List<DateInfo> getPreviousDateInfos(String date) {
        return getPreviousDateInfos(date, PREVIOUS_PERIODS);
    }

    public static List<DateInfo> getPreviousDateInfos(String date, int offset) {
        if (date == null) {
            date = YearMonth.now().toString();
        }

        List<DateInfo> dateInfos;
        if (date.matches(DatePattern.YEAR_REGEX)) {
            Year currentYear = Year.parse(date);
            dateInfos = IntStream.rangeClosed(1, offset)
                    .mapToObj(currentYear::minusYears)
                    .map(DateInfoProvider::getYearInformation)
                    .map(yearInfo -> (DateInfo) yearInfo)
                    .collect(Collectors.toCollection(ArrayList::new));
        } else if (date.matches(DatePattern.YEAR_MONTH_REGEX)) {
            YearMonth yearMonth = YearMonth.parse(date);
            dateInfos = IntStream.rangeClosed(1, offset)
                    .mapToObj(yearMonth::minusMonths)
                    .map(DateInfoProvider::getMonthInformation)
                    .map(monthInfo -> (DateInfo) monthInfo)
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            throw new BadRequestException(DateResponse.UNSUPPORTED_DATE_FORMAT);
        }

        Collections.reverse(dateInfos);
        dateInfos.add(getDateInfo(date));
        return dateInfos;
    }

    private static YearInformation getYearInformation(Year year) {
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

    private static MonthInformation getMonthInformation(YearMonth yearMonth) {
        return MonthInformation.builder()
                .yearMonth(yearMonth)
                .startDate(yearMonth.atDay(DateConstants.FIRST_DAY_OF_MONTH))
                .endDate(yearMonth.atEndOfMonth())
                .weeks(DateInfoProvider.getWeeksInformation(yearMonth))
                .build();
    }

    private static List<WeekInformation> getWeeksInformation(YearMonth yearMonth) {
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
