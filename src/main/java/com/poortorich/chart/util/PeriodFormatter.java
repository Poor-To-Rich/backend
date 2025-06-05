package com.poortorich.chart.util;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class PeriodFormatter {

    private static final DateTimeFormatter DOT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yy.MM.dd");
    private static final DateTimeFormatter DOT_MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("MM.dd");
    private static final String TILDE_DATE_RANGE_SEPARATOR = "~";
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

    public static String formatMonthKorean(Month currentMonth) {
        return currentMonth.getValue() + MONTH_KOREAN_SUFFIX;
    }
}
