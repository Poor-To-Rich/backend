package com.poortorich.iteration.util;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.expense.entity.Expense;
import com.poortorich.income.entity.Income;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.iteration.entity.IterationExpenses;
import com.poortorich.iteration.entity.IterationIncomes;
import com.poortorich.iteration.entity.enums.EndType;
import com.poortorich.iteration.entity.enums.MonthlyMode;
import com.poortorich.iteration.entity.info.DailyIterationRule;
import com.poortorich.iteration.entity.info.IterationInfo;
import com.poortorich.iteration.entity.info.MonthlyIterationRule;
import com.poortorich.iteration.entity.info.WeeklyIterationRule;
import com.poortorich.iteration.entity.info.YearlyIterationRule;
import com.poortorich.iteration.request.CustomIteration;
import com.poortorich.iteration.request.MonthlyOption;
import com.poortorich.iteration.response.EndInfoResponse;
import com.poortorich.iteration.response.IterationRuleInfoResponse;
import com.poortorich.iteration.response.MonthlyOptionInfoResponse;
import com.poortorich.user.entity.User;
import org.hibernate.Hibernate;

public class IterationBuilder {

    public static Iteration buildEntity(
            User user,
            AccountBook originalAccountBook,
            AccountBook generatedAccountBook,
            IterationInfo iterationInfo,
            AccountBookType type
    ) {
        return switch (type) {
            case EXPENSE -> IterationBuilder
                    .buildIterationExpenses(user, (Expense) originalAccountBook, (Expense) generatedAccountBook, iterationInfo);
            case INCOME -> IterationBuilder
                    .buildIterationIncomes(user, (Income) originalAccountBook, (Income) generatedAccountBook, iterationInfo);
        };
    }

    private static IterationExpenses buildIterationExpenses(
            User user,
            Expense originalExpense,
            Expense generatedExpense,
            IterationInfo iterationInfo
    ) {
        return IterationExpenses.builder()
                .originalExpense(originalExpense)
                .generatedExpense(generatedExpense)
                .iterationInfo(iterationInfo)
                .user(user)
                .build();
    }

    private static IterationIncomes buildIterationIncomes(
            User user,
            Income originalIncome,
            Income generatedIncome,
            IterationInfo iterationInfo
    ) {
        return IterationIncomes.builder()
                .originalIncome(originalIncome)
                .generatedIncome(generatedIncome)
                .iterationInfo(iterationInfo)
                .user(user)
                .build();
    }

    public static DailyIterationRule buildDailyIterationInfo(CustomIteration customIteration) {
        return DailyIterationRule.builder()
                .cycle(customIteration.getCycle())
                .endType(customIteration.getEnd().parseEndType())
                .endCount(customIteration.getEnd().getCount())
                .endDate(customIteration.getEnd().parseDate())
                .build();
    }

    public static WeeklyIterationRule buildWeeklyIterationInfo(CustomIteration customIteration) {
        return WeeklyIterationRule.builder()
                .daysOfWeek(customIteration.getIterationRule().parseDaysOfWeek())
                .cycle(customIteration.getCycle())
                .endType(customIteration.getEnd().parseEndType())
                .endCount(customIteration.getEnd().getCount())
                .endDate(customIteration.getEnd().parseDate())
                .build();
    }

    public static MonthlyIterationRule buildMonthlyIterationInfo(CustomIteration customIteration,
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

    public static YearlyIterationRule buildYearlyIterationInfo(CustomIteration customIteration) {
        return YearlyIterationRule.builder()
                .cycle(customIteration.getCycle())
                .endType(customIteration.getEnd().parseEndType())
                .endCount(customIteration.getEnd().getCount())
                .endDate(customIteration.getEnd().parseDate())
                .build();
    }

    public static IterationRuleInfoResponse buildIterationRuleByRuleType(IterationInfo info) {
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

    public static MonthlyOptionInfoResponse buildMonthlyOptionInfoResponseByMonthlyMode(MonthlyIterationRule monthly) {
        return MonthlyOptionInfoResponse.builder()
                .mode(monthly.getMonthlyMode().toString())
                .day(monthly.getMonthlyDay())
                .week(monthly.getMonthlyWeek())
                .dayOfWeek(monthly.getMonthlyDayOfWeek().toString())
                .build();
    }

    public static EndInfoResponse buildEndInfoResponseByEndType(IterationInfo info) {
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
