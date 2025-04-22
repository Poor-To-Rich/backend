package com.poortorich.iteration.service;

import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.entity.enums.IterationType;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.entity.IterationExpenses;
import com.poortorich.iteration.entity.enums.Weekday;
import com.poortorich.iteration.entity.enums.EndType;
import com.poortorich.iteration.entity.enums.IterationRuleType;
import com.poortorich.iteration.entity.enums.MonthlyMode;
import com.poortorich.iteration.entity.info.DailyIterationRule;
import com.poortorich.iteration.entity.info.IterationInfo;
import com.poortorich.iteration.entity.info.MonthlyIterationRule;
import com.poortorich.iteration.entity.info.WeeklyIterationRule;
import com.poortorich.iteration.entity.info.YearlyIterationRule;
import com.poortorich.iteration.repository.IterationExpensesRepository;
import com.poortorich.iteration.repository.IterationInfoRepository;
import com.poortorich.iteration.request.CustomIteration;
import com.poortorich.iteration.request.End;
import com.poortorich.iteration.request.IterationRule;
import com.poortorich.iteration.request.MonthlyOption;
import com.poortorich.iteration.response.IterationResponse;
import com.poortorich.iteration.util.IterationDateCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IterationService {

    private final IterationDateCalculator dateCalculator;
    private final IterationExpensesRepository iterationExpensesRepository;
    private final IterationInfoRepository iterationInfoRepository;

    public List<Expense> createIterationExpenses(CustomIteration customIteration,
                                                 Expense expense) {
        LocalDate startDate = expense.getExpenseDate();
        return getIterationExpenses(
                customIteration,
                startDate,
                expense
        );
    }

    private List<Expense> getIterationExpenses(CustomIteration customIteration, LocalDate startDate, Expense expense) {
        List<Expense> iterationExpenses = new ArrayList<>();
        LocalDate date = getDateByIterationType(customIteration, startDate, expense.getIterationType());
        LocalDate endDate = calculateEndDate(customIteration, expense, startDate);
        int maxIterations = 0;
        int allowedIterations = getAllowedIterations(expense.getIterationType(), customIteration);

        while (!date.isAfter(endDate)) {
            if (maxIterations > allowedIterations) {
                throw new BadRequestException(IterationResponse.ITERATIONS_TOO_LONG);
            }
            Expense generatedExpense = buildIterationExpense(expense, date);
            iterationExpenses.add(generatedExpense);
            date = getDateByIterationType(customIteration, date, expense.getIterationType());
            maxIterations++;
        }

        return iterationExpenses;
    }

    private LocalDate getDateByIterationType(CustomIteration customIteration, LocalDate date, IterationType type) {
        if (type == IterationType.CUSTOM) {
            IterationRule rule = customIteration.getIterationRule();
            return calculateDateByRuleType(customIteration.getCycle(), date, rule, rule.getMonthlyOption());
        }

        return calculateDateByIterationType(type, date);
    }

    private int getAllowedIterations(IterationType type, CustomIteration customIteration) {
        if (type == IterationType.CUSTOM) {
            return customIteration.getIterationRule().parseIterationType().maxIterations;
        }

        return IterationRuleType.DAILY.maxIterations;
    }

    private LocalDate calculateDateByIterationType(IterationType type, LocalDate date) {
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
            return dateCalculator.yearlyTypeDate(date, 1);
        }

        if (type == IterationType.WEEKDAY) {
            return dateCalculator.weeklyTypeDate(date, 1, List.of(Weekday.MONDAY, Weekday.TUESDAY, Weekday.WEDNESDAY, Weekday.THURSDAY, Weekday.FRIDAY, Weekday.SATURDAY, Weekday.SUNDAY));
        }

        return dateCalculator.monthlyTypeEndModeDate(date);
    }

    private LocalDate calculateDateByRuleType(int count,
                                              LocalDate date,
                                              IterationRule rule,
                                              MonthlyOption monthlyOption) {
        if (rule.parseIterationType() == IterationRuleType.DAILY) {
            return dateCalculator.dailyTypeDate(date, count);
        }

        if (rule.parseIterationType() == IterationRuleType.WEEKLY) {
            return dateCalculator.weeklyTypeDate(date, count, rule.DaysOfWeekToList());
        }

        if (rule.parseIterationType() == IterationRuleType.MONTHLY) {
            return processCalculatorByMonthlyRule(
                    date,
                    count,
                    monthlyOption.getDay(),
                    monthlyOption.getWeek(),
                    monthlyOption.parseMonthlyMode(),
                    monthlyOption.parseDayOfWeek()
            );
        }

        return dateCalculator.yearlyTypeDate(date, count);
    }

    private LocalDate processCalculatorByMonthlyRule(LocalDate date,
                                                     int count,
                                                     int day,
                                                     int week,
                                                     MonthlyMode mode,
                                                     Weekday weekday) {
        LocalDate targetDate = date.plusMonths(count);

        if (mode == MonthlyMode.DAY) {
            return dateCalculator.monthlyTypeDayModeDate(targetDate, day);
        }

        if (mode == MonthlyMode.WEEKDAY) {
            return dateCalculator.monthlyTypeWeekDayModeDate(targetDate, week, weekday);
        }

        if (mode == MonthlyMode.END) {
            return dateCalculator.monthlyTypeEndModeDate(targetDate);
        }

        return targetDate;
    }

    private LocalDate calculateEndDate(CustomIteration customIteration, Expense expense, LocalDate startDate) {
        if (expense.getIterationType() == IterationType.CUSTOM) {
            return calculateEndDate(customIteration.getEnd(), customIteration, startDate);
        }

        if (expense.getIterationType() == IterationType.DAILY) {
            return dateCalculator.yearlyTypeDate(startDate, 3);
        }

        return dateCalculator.yearlyTypeDate(startDate, 10);
    }

    private LocalDate calculateEndDate(End end, CustomIteration customIteration, LocalDate startDate) {
        if (end.parseEndType() == EndType.NEVER) {
            return dateCalculator.yearlyTypeDate(startDate, 10);
        }

        if (end.parseEndType() == EndType.AFTER) {
            return calculateDateByRuleType(end.getCount() * customIteration.getCycle(), startDate, customIteration.getIterationRule(), customIteration.getIterationRule().getMonthlyOption());
        }

        return end.getDate();
    }

    private Expense buildIterationExpense(Expense originalExpense, LocalDate date) {
        return Expense.builder()
                .expenseDate(date)
                .title(originalExpense.getTitle())
                .cost(originalExpense.getCost())
                .paymentMethod(originalExpense.getPaymentMethod())
                .memo(originalExpense.getMemo())
                .iterationType(originalExpense.getIterationType())
                .category(originalExpense.getCategory())
                .build();
    }

    public void createIterationInfo(CustomIteration customIteration,
                                    Expense originalExpense,
                                    List<Expense> savedIterationExpenses) {
        IterationInfo iterationInfo = getIterationInfo(customIteration);
        iterationInfoRepository.save(iterationInfo);

        for (Expense savedExpense : savedIterationExpenses) {
            IterationExpenses iterationExpenses = buildIterationExpenses(originalExpense, savedExpense, iterationInfo);
            iterationExpensesRepository.save(iterationExpenses);
        }
    }

    private IterationInfo getIterationInfo(CustomIteration customIteration) {
        IterationRuleType ruleType = customIteration.getIterationRule().parseIterationType();

        if (ruleType == IterationRuleType.DAILY) {
            return buildDailyIterationInfo(customIteration);
        }
        if (ruleType == IterationRuleType.WEEKLY) {
            return buildWeeklyIterationInfo(customIteration);
        }
        if (ruleType == IterationRuleType.MONTHLY) {
            return buildMonthlyIterationInfo(customIteration, customIteration.getIterationRule().getMonthlyOption());
        }
        if (ruleType == IterationRuleType.YEARLY) {
            return buildYearlyIterationInfo(customIteration);
        }

        throw new BadRequestException(IterationResponse.ITERATION_RULE_TYPE_INVALID);
    }

    private DailyIterationRule buildDailyIterationInfo(CustomIteration customIteration) {
        return DailyIterationRule.builder()
                .cycle(customIteration.getCycle())
                .endType(customIteration.getEnd().parseEndType())
                .endCount(customIteration.getEnd().getCount())
                .endDate(customIteration.getEnd().getDate())
                .build();
    }

    private WeeklyIterationRule buildWeeklyIterationInfo(CustomIteration customIteration) {
        return WeeklyIterationRule.builder()
                .daysOfWeek(customIteration.getIterationRule().parseDaysOfWeek())
                .cycle(customIteration.getCycle())
                .endType(customIteration.getEnd().parseEndType())
                .endCount(customIteration.getEnd().getCount())
                .endDate(customIteration.getEnd().getDate())
                .build();
    }

    private MonthlyIterationRule buildMonthlyIterationInfo(CustomIteration customIteration,
                                                           MonthlyOption monthlyOption) {
        return MonthlyIterationRule.builder()
                .monthlyMode(monthlyOption.parseMonthlyMode())
                .monthlyDay(monthlyOption.getDay())
                .monthlyWeek(monthlyOption.getWeek())
                .monthlyDayOfWeek(monthlyOption.parseDayOfWeek())
                .cycle(customIteration.getCycle())
                .endType(customIteration.getEnd().parseEndType())
                .endCount(customIteration.getEnd().getCount())
                .endDate(customIteration.getEnd().getDate())
                .build();
    }

    private YearlyIterationRule buildYearlyIterationInfo(CustomIteration customIteration) {
        return YearlyIterationRule.builder()
                .cycle(customIteration.getCycle())
                .endType(customIteration.getEnd().parseEndType())
                .endCount(customIteration.getEnd().getCount())
                .endDate(customIteration.getEnd().getDate())
                .build();
    }

    private IterationExpenses buildIterationExpenses(Expense originalExpense,
                                                     Expense generatedExpense,
                                                     IterationInfo iterationInfo) {
        return IterationExpenses.builder()
                .originalExpense(originalExpense)
                .generatedExpense(generatedExpense)
                .iterationInfo(iterationInfo)
                .build();
    }
}
