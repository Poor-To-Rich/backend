package com.poortorich.iteration.provider;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.iteration.entity.enums.EndType;
import com.poortorich.iteration.entity.enums.IterationRuleType;
import com.poortorich.iteration.entity.enums.MonthlyMode;
import com.poortorich.iteration.entity.enums.Weekday;
import com.poortorich.iteration.request.CustomIteration;
import com.poortorich.iteration.request.End;
import com.poortorich.iteration.request.IterationRule;
import com.poortorich.iteration.request.MonthlyOption;
import com.poortorich.iteration.util.IterationDateCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IterationCalculatorProvider {

    private final IterationDateCalculator dateCalculator;

    public LocalDate calculateDateByRuleType(
            int cycle, LocalDate date, LocalDate startDate, IterationRule rule, MonthlyOption monthlyOption) {
        if (rule.parseIterationType() == IterationRuleType.DAILY) {
            return dateCalculator.dailyTypeDate(date, cycle);
        }

        if (rule.parseIterationType() == IterationRuleType.WEEKLY) {
            return dateCalculator.weeklyTypeDate(date, cycle, rule.daysOfWeekToList());
        }

        if (rule.parseIterationType() == IterationRuleType.MONTHLY) {
            return calculateDateByMonthlyMode(
                    date, cycle, monthlyOption, monthlyOption.parseMonthlyMode());
        }

        return dateCalculator.yearlyTypeDate(date, cycle, startDate);
    }

    private LocalDate calculateDateByMonthlyMode(
            LocalDate date, int cycle, MonthlyOption option, MonthlyMode mode) {
        LocalDate targetDate = date.plusMonths(cycle);

        if (mode == MonthlyMode.DAY) {
            return dateCalculator.monthlyTypeDayModeDate(targetDate, option.getDay());
        }

        if (mode == MonthlyMode.WEEKDAY) {
            return dateCalculator.monthlyTypeWeekDayModeDate(targetDate, option.getWeek(), option.parseDayOfWeek());
        }

        if (mode == MonthlyMode.END) {
            return dateCalculator.monthlyTypeEndModeDate(targetDate);
        }

        return targetDate;
    }

    public LocalDate calculateEndDateByType(CustomIteration customIteration, AccountBook accountBook, LocalDate startDate) {
        if (accountBook.getIterationType() == IterationType.CUSTOM) {
            return calculateEndDateByEnd(customIteration.getEnd(), customIteration, startDate);
        }

        if (accountBook.getIterationType() == IterationType.DAILY || accountBook.getIterationType() == IterationType.WEEKDAY) {
            return dateCalculator.yearlyTypeDate(startDate, 3, startDate);
        }

        return dateCalculator.yearlyTypeDate(startDate, 10, startDate);
    }

    private LocalDate calculateEndDateByEnd(End end, CustomIteration customIteration, LocalDate startDate) {
        if (end.parseEndType() == EndType.NEVER) {
            return defaultEndDateByRule(customIteration.getIterationRule().parseIterationType(), startDate);
        }

        if (end.parseEndType() == EndType.AFTER) {
            IterationRule rule = customIteration.getIterationRule();

            if (rule.parseIterationType() == IterationRuleType.WEEKLY) {
                return dateCalculator.weeklyEndDate(
                        startDate, end.getCount() * customIteration.getCycle() - 1, rule.daysOfWeekToList());
            }

            return calculateDateByRuleType(
                    end.getCount() * customIteration.getCycle() - 1, startDate, startDate, rule, rule.getMonthlyOption()
            );
        }

        return end.parseDate();
    }

    private LocalDate defaultEndDateByRule(IterationRuleType rule, LocalDate startDate) {
        if (rule == IterationRuleType.DAILY) {
            return dateCalculator.yearlyTypeDate(startDate, 3, startDate);
        }

        if (rule == IterationRuleType.WEEKLY) {
            return dateCalculator.yearlyTypeDate(startDate, 5, startDate);
        }

        return dateCalculator.yearlyTypeDate(startDate, 10, startDate);
    }

    public LocalDate defaultCalculateDateByType(IterationType type, LocalDate date, LocalDate startDate) {
        if (type == IterationType.DAILY) {
            return dateCalculator.dailyTypeDate(date, 1);
        }

        if (type == IterationType.WEEKLY) {
            return dateCalculator.weeklyTypeDate(date, 1);
        }

        if (type == IterationType.MONTHLY) {
            return dateCalculator.monthlyTypeDate(date, 1);
        }

        if (type == IterationType.YEARLY) {
            return dateCalculator.yearlyTypeDate(date, 1, startDate);
        }

        if (type == IterationType.WEEKDAY) {
            List<Weekday> allWeekdays = Weekday.getWeekdays();
            return dateCalculator.weeklyTypeDate(date, 1, allWeekdays);
        }

        return dateCalculator.monthlyTypeEndModeDate(date);
    }
}
