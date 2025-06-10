package com.poortorich.chart.util;

import com.poortorich.global.date.constants.DatePattern;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class PeriodFormatter {

    private static final DateTimeFormatter DOT_DATE_FORMATTER
            = DateTimeFormatter.ofPattern(DatePattern.LOCAL_DATE_DOT_PATTERN);
    private static final DateTimeFormatter DOT_MONTH_DAY_FORMATTER
            = DateTimeFormatter.ofPattern(DatePattern.MONTH_DAY_DOT_PATTERN);

    private static final String TILDE_DATE_RANGE_SEPARATOR = "~";
    private static final String TILDE_DATE_RANGE_SEPARATOR_WITH_SPACE = " ~ ";
    private static final String MONTH_KOREAN_SUFFIX = "월";

    public static String formatLocalDateRange(LocalDate startDate, LocalDate endDate) {
        return startDate.format(DOT_DATE_FORMATTER)
                + TILDE_DATE_RANGE_SEPARATOR
                + endDate.format(DOT_DATE_FORMATTER);
    }

    public static String formatMonthDayRange(LocalDate startDate, LocalDate endDate) {
        return startDate.format(DOT_MONTH_DAY_FORMATTER)
                + TILDE_DATE_RANGE_SEPARATOR
                + endDate.format(DOT_MONTH_DAY_FORMATTER);
    }

    public static String formatWeeklyReportRange(LocalDate startDate, LocalDate endDate) {
        return startDate.format(DOT_DATE_FORMATTER)
                + TILDE_DATE_RANGE_SEPARATOR_WITH_SPACE
                + endDate.format(DOT_DATE_FORMATTER);
    }

    public static String formatMonthKorean(Month currentMonth) {
        return currentMonth.getValue() + MONTH_KOREAN_SUFFIX;
    }
}
