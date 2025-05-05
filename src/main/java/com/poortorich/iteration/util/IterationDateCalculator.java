package com.poortorich.iteration.util;

import com.poortorich.iteration.entity.enums.Weekday;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
public class IterationDateCalculator {

    public LocalDate dailyTypeDate(LocalDate date, int cycle) {
        return date.plusDays(cycle);
    }

    public LocalDate weeklyEndDate(LocalDate startDate, int count, List<Weekday> selectDays) {
        Weekday lastDayOfWeek = selectDays.getLast();
        Weekday startDayOfWeek = Weekday.fromDayOfWeek(startDate.getDayOfWeek());

        int dayToAdd = Math.max(lastDayOfWeek.ordinal() - startDayOfWeek.ordinal(), 0);

        return startDate.plusWeeks(count).plusDays(dayToAdd);
    }

    public LocalDate weeklyTypeDate(LocalDate date, int cycle, List<Weekday> selectDays) {
        Weekday currentDayOfWeek = Weekday.fromDayOfWeek(date.getDayOfWeek());

        for (Weekday day : selectDays) {
            int dayToAdd = day.ordinal() - currentDayOfWeek.ordinal();
            if (dayToAdd > 0) {
                return date.plusDays(dayToAdd);
            }
        }

        LocalDate cycleDate = date.plusWeeks(cycle - 1);
        return weeklyTypeNextWeekFirstDay(cycleDate, selectDays.getFirst());
    }

    private LocalDate weeklyTypeNextWeekFirstDay(LocalDate date, Weekday firstDay) {
        Weekday currentDay = Weekday.fromDayOfWeek(date.getDayOfWeek());
        return date.plusDays((7 - currentDay.ordinal()) + firstDay.ordinal());
    }

    public LocalDate weeklyTypeDate(LocalDate date, int cycle) {
        return date.plusWeeks(cycle);
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

    public LocalDate monthlyTypeDate(LocalDate date, int cycle) {
        return date.plusMonths(cycle);
    }

    public LocalDate yearlyTypeDate(LocalDate date, int cycle) {
        return date.plusYears(cycle);
    }
}
