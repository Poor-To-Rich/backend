package com.poortorich.global.date.fixture;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;

public class DateTestFixture {

    public static final Year TEST_YEAR = Year.of(2025);

    public static final YearMonth MAY_2025 = YearMonth.of(2025, 5);

    public static final LocalDate MAY_START = MAY_2025.atDay(1);
    public static final LocalDate MAY_END = MAY_2025.atEndOfMonth();

    public static final LocalDate YEAR_START = TEST_YEAR.atDay(1);
    public static final LocalDate YEAR_END = TEST_YEAR.atMonth(12).atEndOfMonth();

    public static final DayOfWeek WEEK_START_DAY = DayOfWeek.SUNDAY;
    public static final DayOfWeek WEEK_END_DAY = DayOfWeek.SATURDAY;

    public static final LocalDate MAY_2025_WEEK_FIRST_START = LocalDate.of(2025, 4, 27);
    public static final LocalDate MAY_2025_WEEK_FIRST_END = LocalDate.of(2025, 5, 3);

    public static final LocalDate MAY_2025_WEEK_LAST_START = LocalDate.of(2025, 5, 25);
    public static final LocalDate MAY_2025_WEEK_LAST_END = LocalDate.of(2025, 5, 31);
}
