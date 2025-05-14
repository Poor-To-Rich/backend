package com.poortorich.global.util;

import com.poortorich.global.domain.MonthInformation;
import com.poortorich.global.domain.WeekInformation;
import com.poortorich.global.domain.YearInformation;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

public class DateInfoProvider {

    public static YearInformation getYearInformation(Year year) {

    }

    public static MonthInformation getMonthInformation(YearMonth yearMonth) {

    }

    public static List<WeekInformation> getWeekInformation(YearMonth yearMonth) {
        int lengthOfMonth = yearMonth.lengthOfMonth();

    }
}
