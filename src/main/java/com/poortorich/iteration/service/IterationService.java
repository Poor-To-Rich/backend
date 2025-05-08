package com.poortorich.iteration.service;

import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.entity.enums.IterationType;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.NotFoundException;
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
import com.poortorich.iteration.response.CustomIterationInfoResponse;
import com.poortorich.iteration.response.EndInfoResponse;
import com.poortorich.iteration.response.IterationResponse;
import com.poortorich.iteration.response.IterationRuleInfoResponse;
import com.poortorich.iteration.response.MonthlyOptionInfoResponse;
import com.poortorich.iteration.util.IterationDateCalculator;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.response.enums.UserResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IterationService {

    private final IterationDateCalculator dateCalculator;
    private final IterationExpensesRepository iterationExpensesRepository;
    private final IterationInfoRepository iterationInfoRepository;
    private final UserRepository userRepository;

    public List<Expense> createIterationExpenses(CustomIteration customIteration,
                                                 Expense expense,
                                                 String username) {
        User user = findUserByUsername(username);
        LocalDate startDate = expense.getExpenseDate();
        return getIterationExpenses(
                customIteration,
                startDate,
                expense,
                user
        );
    }

    private List<Expense> getIterationExpenses(CustomIteration customIteration, LocalDate startDate, Expense expense, User user) {
        List<Expense> iterationExpenses = new ArrayList<>();
        LocalDate date = getDateByIterationType(customIteration, startDate, expense.getIterationType());
        LocalDate endDate = calculateEndDate(customIteration, expense, startDate);
        int maxIterations = 0;
        int allowedIterations = getAllowedIterations(expense.getIterationType(), customIteration);

        iterationExpenses.add(expense);
        while (!date.isAfter(endDate)) {
            if (maxIterations > allowedIterations) {
                throw new BadRequestException(IterationResponse.ITERATIONS_TOO_LONG);
            }
            Expense generatedExpense = buildIterationExpense(expense, date, user);
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

    private LocalDate calculateEndDate(CustomIteration customIteration, Expense expense, LocalDate startDate) {
        if (expense.getIterationType() == IterationType.CUSTOM) {
            return calculateEndDate(customIteration.getEnd(), customIteration, startDate);
        }

        if (expense.getIterationType() == IterationType.DAILY || expense.getIterationType() == IterationType.WEEKDAY) {
            return dateCalculator.yearlyTypeDate(startDate, 3);
        }

        return dateCalculator.yearlyTypeDate(startDate, 10);
    }

    private LocalDate calculateEndDate(End end, CustomIteration customIteration, LocalDate startDate) {
        if (end.parseEndType() == EndType.NEVER) {
            return calculateEndDateByIterationRule(customIteration.getIterationRule().parseIterationType(), startDate);
        }

        if (end.parseEndType() == EndType.AFTER) {
            IterationRule rule = customIteration.getIterationRule();

            if (rule.parseIterationType() == IterationRuleType.WEEKLY) {
                return dateCalculator.weeklyEndDate(startDate, end.getCount() * customIteration.getCycle() - 1, rule.daysOfWeekToList());
            }

            return calculateDateByRuleType(end.getCount() * customIteration.getCycle(), startDate, rule, rule.getMonthlyOption());
        }

        return end.parseDate();
    }

    private LocalDate calculateEndDateByIterationRule(IterationRuleType rule, LocalDate startDate) {
        if (rule == IterationRuleType.DAILY) {
            return dateCalculator.yearlyTypeDate(startDate, 3);
        }

        if (rule == IterationRuleType.WEEKLY) {
            return dateCalculator.yearlyTypeDate(startDate, 5);
        }

        return dateCalculator.yearlyTypeDate(startDate, 10);
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
            List<Weekday> allWeekdays = new ArrayList<>(EnumSet.allOf(Weekday.class));
            return dateCalculator.weeklyTypeDate(date, 1, allWeekdays);
        }

        return dateCalculator.monthlyTypeEndModeDate(date);
    }

    private LocalDate calculateDateByRuleType(int cycle,
                                              LocalDate date,
                                              IterationRule rule,
                                              MonthlyOption monthlyOption) {
        if (rule.parseIterationType() == IterationRuleType.DAILY) {
            return dateCalculator.dailyTypeDate(date, cycle);
        }

        if (rule.parseIterationType() == IterationRuleType.WEEKLY) {
            return dateCalculator.weeklyTypeDate(date, cycle, rule.daysOfWeekToList());
        }

        if (rule.parseIterationType() == IterationRuleType.MONTHLY) {
            return processCalculatorByMonthlyRule(
                    date,
                    cycle,
                    monthlyOption,
                    monthlyOption.parseMonthlyMode()
            );
        }

        return dateCalculator.yearlyTypeDate(date, cycle);
    }

    private LocalDate processCalculatorByMonthlyRule(LocalDate date,
                                                     int cycle,
                                                     MonthlyOption option,
                                                     MonthlyMode mode) {
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

    private Expense buildIterationExpense(Expense originalExpense, LocalDate date, User user) {
        return Expense.builder()
                .expenseDate(date)
                .title(originalExpense.getTitle())
                .cost(originalExpense.getCost())
                .paymentMethod(originalExpense.getPaymentMethod())
                .memo(originalExpense.getMemo())
                .iterationType(originalExpense.getIterationType())
                .category(originalExpense.getCategory())
                .user(user)
                .build();
    }

    public void createIterationInfo(CustomIteration customIteration,
                                    Expense originalExpense,
                                    List<Expense> savedIterationExpenses,
                                    String username) {
        User user = findUserByUsername(username);
        IterationInfo iterationInfo = getIterationInfo(customIteration);
        iterationInfoRepository.save(iterationInfo);

        for (Expense savedExpense : savedIterationExpenses) {
            IterationExpenses iterationExpenses = buildIterationExpenses(originalExpense, savedExpense, iterationInfo, user);
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
                .endDate(customIteration.getEnd().parseDate())
                .build();
    }

    private WeeklyIterationRule buildWeeklyIterationInfo(CustomIteration customIteration) {
        return WeeklyIterationRule.builder()
                .daysOfWeek(customIteration.getIterationRule().parseDaysOfWeek())
                .cycle(customIteration.getCycle())
                .endType(customIteration.getEnd().parseEndType())
                .endCount(customIteration.getEnd().getCount())
                .endDate(customIteration.getEnd().parseDate())
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
                .endDate(customIteration.getEnd().parseDate())
                .build();
    }

    private YearlyIterationRule buildYearlyIterationInfo(CustomIteration customIteration) {
        return YearlyIterationRule.builder()
                .cycle(customIteration.getCycle())
                .endType(customIteration.getEnd().parseEndType())
                .endCount(customIteration.getEnd().getCount())
                .endDate(customIteration.getEnd().parseDate())
                .build();
    }

    private IterationExpenses buildIterationExpenses(Expense originalExpense,
                                                     Expense generatedExpense,
                                                     IterationInfo iterationInfo,
                                                     User user) {
        return IterationExpenses.builder()
                .originalExpense(originalExpense)
                .generatedExpense(generatedExpense)
                .iterationInfo(iterationInfo)
                .user(user)
                .build();
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND));
    }

    public CustomIterationInfoResponse getCustomIteration(IterationExpenses iterationExpenses) {
        IterationInfo iterationInfo = iterationExpenses.getIterationInfo();
        return CustomIterationInfoResponse.builder()
                .iterationRule(buildIterationRuleByRuleType(iterationInfo))
                .cycle(iterationInfo.getCycle())
                .end(buildEndInfoResponseByEndType(iterationInfo))
                .build();
    }

    private IterationRuleInfoResponse buildIterationRuleByRuleType(IterationInfo info) {
        IterationInfo unproxiedInfo = (IterationInfo) Hibernate.unproxy(info);

        if (unproxiedInfo instanceof WeeklyIterationRule weekly) {
            return IterationRuleInfoResponse.builder()
                    .type(weekly.getIterationTypeLowerCase())
                    .daysOfWeek(weekly.getDaysOfWeekList())
                    .build();
        }

        if (unproxiedInfo instanceof MonthlyIterationRule monthly) {
            return IterationRuleInfoResponse.builder()
                    .type(monthly.getIterationTypeLowerCase())
                    .monthlyOption(buildMonthlyOptionInfoResponseByMonthlyMode(monthly))
                    .build();
        }

        return IterationRuleInfoResponse.builder()
                .type(info.getIterationTypeLowerCase())
                .build();
    }

    private MonthlyOptionInfoResponse buildMonthlyOptionInfoResponseByMonthlyMode(MonthlyIterationRule monthly) {
        if (monthly.getMonthlyMode() == MonthlyMode.DAY) {
            return MonthlyOptionInfoResponse.builder()
                    .mode(monthly.getMonthlyMode().toString())
                    .day(monthly.getMonthlyDay())
                    .week(monthly.getMonthlyWeek())
                    .build();
        }

        if (monthly.getMonthlyMode() == MonthlyMode.WEEKDAY) {
            return MonthlyOptionInfoResponse.builder()
                    .mode(monthly.getMonthlyMode().toString())
                    .dayOfWeek(monthly.getMonthlyDayOfWeek().toString())
                    .build();
        }

        return MonthlyOptionInfoResponse.builder()
                .mode(monthly.getMonthlyMode().toString())
                .build();
    }

    private EndInfoResponse buildEndInfoResponseByEndType(IterationInfo info) {
        if (info.getEndType() == EndType.AFTER) {
            return EndInfoResponse.builder()
                    .type(info.getEndType().toString())
                    .count(info.getEndCount())
                    .build();
        }

        if (info.getEndType() == EndType.UNTIL) {
            return EndInfoResponse.builder()
                    .type(info.getEndType().toString())
                    .date(info.getEndDate())
                    .build();
        }

        return EndInfoResponse.builder()
                .type(info.getEndType().toString())
                .build();
    }
}
