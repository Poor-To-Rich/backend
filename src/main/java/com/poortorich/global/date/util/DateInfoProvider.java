package com.poortorich.global.date.util;

import com.poortorich.global.date.constants.DateConstants;
import com.poortorich.global.date.constants.DatePattern;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.domain.WeekInformation;
import com.poortorich.global.date.domain.YearInformation;
import com.poortorich.global.date.response.enums.DateResponse;
import com.poortorich.global.exceptions.BadRequestException;
import io.jsonwebtoken.lang.Objects;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DateInfoProvider {

    private static final Map<Predicate<String>, Function<String, DateInfo>> DATE_PARSER = new LinkedHashMap<>();
    private static final int PREVIOUS_PERIODS = 5;

    static {
        DATE_PARSER.put(
                Objects::isEmpty,
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

    public static DateInfo get(String date) {
        return DATE_PARSER.entrySet().stream()
                .filter(entry -> entry.getKey().test(date))
                .findFirst()
                .map(entry -> entry.getValue().apply(date))
                .orElseThrow(() -> new BadRequestException(DateResponse.UNSUPPORTED_DATE_FORMAT));
    }

    public static DateInfo get(YearMonth yearMonth) {
        return getMonthInformation(yearMonth);
    }

    public static DateInfo get(Year year) {
        return getYearInformation(year);
    }

    public static List<DateInfo> getPreviousAndCurrent(String date) {
        return getPreviousAndCurrent(date, PREVIOUS_PERIODS);
    }

    public static List<DateInfo> getPreviousAndCurrent(String date, int offset) {
        if (Objects.isEmpty(date) || date.matches(DatePattern.YEAR_MONTH_REGEX)) {
            return getPreviousAndCurrentMonthInformation(date, offset);
        } else if (date.matches(DatePattern.YEAR_REGEX)) {
            return getPreviousAndCurrentYearInformation(date, offset);
        }
        throw new BadRequestException(DateResponse.UNSUPPORTED_DATE_FORMAT);
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

    private static List<DateInfo> getPreviousAndCurrentYearInformation(String date, int offset) {
        Year year = (Objects.isEmpty(date) ? Year.now() : Year.parse(date));
        Year currentYear = year.minusYears(offset);

        List<DateInfo> dateInfos = new ArrayList<>();
        while (!currentYear.isAfter(year)) {
            dateInfos.add(get(currentYear));
            currentYear = currentYear.plusYears(DateConstants.ONE_YEAR);
        }

        return dateInfos;
    }

    private static List<DateInfo> getPreviousAndCurrentMonthInformation(String date, int offset) {
        YearMonth yearMonth = (Objects.isEmpty(date) ? YearMonth.now() : YearMonth.parse(date));
        YearMonth currentMonth = yearMonth.minusMonths(offset);

        List<DateInfo> dateInfos = new ArrayList<>();
        while (!currentMonth.isAfter(yearMonth)) {
            dateInfos.add(get(currentMonth));
            currentMonth = currentMonth.plusMonths(DateConstants.ONE_DAY);
        }
        return dateInfos;
    }
}
