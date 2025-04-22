package com.poortorich.iteration.util;

import com.poortorich.iteration.entity.enums.Weekday;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
public class IterationDateCalculator {

    public LocalDate dailyTypeDate(LocalDate date, int count) {
        return date.plusDays(count);
    }

    public LocalDate weeklyTypeDate(LocalDate date, int count, List<Weekday> selectDays) {
        DayOfWeek currentDayOfWeek = date.getDayOfWeek();

        LocalDate nextDate;
        for (Weekday day : selectDays) {
            nextDate = weeklyTypeFoundNextDate(date, day, currentDayOfWeek);
            if (nextDate != null) {
                return nextDate;
            }
        }

        return weeklyTypeFoundDateByFirstDay(date, count, selectDays.getFirst().name(), currentDayOfWeek);
    }

    private LocalDate weeklyTypeFoundNextDate(LocalDate date, Weekday day, DayOfWeek currentDayOfWeek) {
        DayOfWeek targetDay = DayOfWeek.valueOf(day.name());
        int daysUntilTarget = targetDay.getValue() - currentDayOfWeek.getValue();
        if (daysUntilTarget > 0) {
            return date.plusDays(daysUntilTarget);
        }

        return null;
    }

    private LocalDate weeklyTypeFoundDateByFirstDay(LocalDate date, int count, String firstDay, DayOfWeek currentDayOfWeek) {
        DayOfWeek firstSelectDay = DayOfWeek.valueOf(firstDay);
        int daysUntilNext = firstSelectDay.getValue() - currentDayOfWeek.getValue();
        if (daysUntilNext <= 0) {
            daysUntilNext += 7;
        }
        return date.plusWeeks(count - 1).plusDays(daysUntilNext);
    }

    public LocalDate weeklyTypeDate(LocalDate date, int count) {
        return date.plusWeeks(count);
    }

    public LocalDate monthlyTypeDayModeDate(LocalDate date, int day) {
        while (day > date.lengthOfMonth()) {
            date = date.plusMonths(1);
        }

        return date.withDayOfMonth(day);
    }

    public LocalDate monthlyTypeWeekDayModeDate(LocalDate date, int week, Weekday weekday) {
        DayOfWeek targetDayOfWeek = DayOfWeek.valueOf(weekday.name());

        if (week == 0) {
            return date.with(TemporalAdjusters.lastInMonth(targetDayOfWeek));
        }

        return date.with(TemporalAdjusters.firstInMonth(targetDayOfWeek))
                .plusWeeks(week - 1);
    }

    public LocalDate monthlyTypeEndModeDate(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    public LocalDate monthlyTypeDate(LocalDate date, int count) {
        return date.plusMonths(count);
    }

    public LocalDate yearlyTypeDate(LocalDate date, int count) {
        return date.plusYears(count);
    }
}
