package com.poortorich.iteration.service;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.request.AccountBookRequest;
import com.poortorich.accountbook.response.AccountBookResponse;
import com.poortorich.accountbook.util.AccountBookBuilder;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.accountbook.request.enums.IterationAction;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.iteration.entity.enums.Weekday;
import com.poortorich.iteration.entity.enums.EndType;
import com.poortorich.iteration.entity.enums.IterationRuleType;
import com.poortorich.iteration.entity.enums.MonthlyMode;
import com.poortorich.iteration.entity.info.DailyIterationRule;
import com.poortorich.iteration.entity.info.IterationInfo;
import com.poortorich.iteration.entity.info.MonthlyIterationRule;
import com.poortorich.iteration.entity.info.WeeklyIterationRule;
import com.poortorich.iteration.entity.info.YearlyIterationRule;
import com.poortorich.iteration.repository.IterationInfoRepository;
import com.poortorich.iteration.repository.IterationRepository;
import com.poortorich.iteration.request.CustomIteration;
import com.poortorich.iteration.request.End;
import com.poortorich.iteration.request.IterationRule;
import com.poortorich.iteration.request.MonthlyOption;
import com.poortorich.iteration.response.CustomIterationInfoResponse;
import com.poortorich.iteration.response.EndInfoResponse;
import com.poortorich.iteration.response.IterationResponse;
import com.poortorich.iteration.response.IterationRuleInfoResponse;
import com.poortorich.iteration.response.MonthlyOptionInfoResponse;
import com.poortorich.iteration.util.IterationBuilder;
import com.poortorich.iteration.util.IterationDateCalculator;
import com.poortorich.user.entity.User;
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

    private static final String MODIFY_TYPE = "modify";
    private static final String DELETE_TYPE = "delete";

    private final IterationDateCalculator dateCalculator;
    private final IterationRepository iterationRepository;
    private final IterationInfoRepository iterationInfoRepository;

    public List<AccountBook> createIterations(
            User user,
            CustomIteration customIteration,
            AccountBook accountBook,
            AccountBookType type
    ) {
        LocalDate startDate = accountBook.getAccountBookDate();
        return getIterations(
                user,
                customIteration,
                startDate,
                accountBook,
                type
        );
    }

    private List<AccountBook> getIterations(
            User user,
            CustomIteration customIteration,
            LocalDate startDate,
            AccountBook accountBook,
            AccountBookType type
    ) {
        List<AccountBook> iterations = new ArrayList<>();
        LocalDate date = getDateByIterationType(customIteration, startDate, startDate, accountBook.getIterationType());
        LocalDate endDate = calculateEndDate(customIteration, accountBook, startDate);
        int maxIterations = 0;
        int allowedIterations = getAllowedIterations(accountBook.getIterationType(), customIteration);

        iterations.add(accountBook);
        while (!date.isAfter(endDate)) {
            if (maxIterations > allowedIterations) {
                throw new BadRequestException(IterationResponse.ITERATIONS_TOO_LONG);
            }
            AccountBook generatedAccountBook = AccountBookBuilder.buildEntity(user, date, accountBook, type);
            iterations.add(generatedAccountBook);
            date = getDateByIterationType(customIteration, date, startDate, accountBook.getIterationType());
            maxIterations++;
        }

        return iterations;
    }

    private LocalDate getDateByIterationType(
            CustomIteration customIteration, LocalDate date, LocalDate startDate, IterationType type) {
        if (type == IterationType.CUSTOM) {
            IterationRule rule = customIteration.getIterationRule();
            return calculateDateByRuleType(customIteration.getCycle(), date, startDate, rule, rule.getMonthlyOption());
        }

        return calculateDateByIterationType(type, date, startDate);
    }

    private LocalDate calculateEndDate(CustomIteration customIteration, AccountBook accountBook, LocalDate startDate) {
        if (accountBook.getIterationType() == IterationType.CUSTOM) {
            return calculateEndDate(customIteration.getEnd(), customIteration, startDate);
        }

        if (accountBook.getIterationType() == IterationType.DAILY || accountBook.getIterationType() == IterationType.WEEKDAY) {
            return dateCalculator.yearlyTypeDate(startDate, 3, startDate);
        }

        return dateCalculator.yearlyTypeDate(startDate, 10, startDate);
    }

    private LocalDate calculateEndDate(End end, CustomIteration customIteration, LocalDate startDate) {
        if (end.parseEndType() == EndType.NEVER) {
            return calculateEndDateByIterationRule(customIteration.getIterationRule().parseIterationType(), startDate);
        }

        if (end.parseEndType() == EndType.AFTER) {
            IterationRule rule = customIteration.getIterationRule();

            if (rule.parseIterationType() == IterationRuleType.WEEKLY) {
                return dateCalculator.weeklyEndDate(
                        startDate, end.getCount() * customIteration.getCycle() - 1, rule.daysOfWeekToList());
            }

            return calculateDateByRuleType(
                    end.getCount() * customIteration.getCycle(), startDate, startDate, rule, rule.getMonthlyOption());
        }

        return end.parseDate();
    }

    private LocalDate calculateEndDateByIterationRule(IterationRuleType rule, LocalDate startDate) {
        if (rule == IterationRuleType.DAILY) {
            return dateCalculator.yearlyTypeDate(startDate, 3, startDate);
        }

        if (rule == IterationRuleType.WEEKLY) {
            return dateCalculator.yearlyTypeDate(startDate, 5, startDate);
        }

        return dateCalculator.yearlyTypeDate(startDate, 10, startDate);
    }

    private int getAllowedIterations(IterationType type, CustomIteration customIteration) {
        if (type == IterationType.CUSTOM) {
            return customIteration.getIterationRule().parseIterationType().maxIterations;
        }

        return IterationRuleType.DAILY.maxIterations;
    }

    private LocalDate calculateDateByIterationType(IterationType type, LocalDate date, LocalDate startDate) {
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
            List<Weekday> allWeekdays = new ArrayList<>(EnumSet.allOf(Weekday.class));
            return dateCalculator.weeklyTypeDate(date, 1, allWeekdays);
        }

        return dateCalculator.monthlyTypeEndModeDate(date);
    }

    private LocalDate calculateDateByRuleType(
            int cycle, LocalDate date, LocalDate startDate, IterationRule rule, MonthlyOption monthlyOption) {
        if (rule.parseIterationType() == IterationRuleType.DAILY) {
            return dateCalculator.dailyTypeDate(date, cycle);
        }

        if (rule.parseIterationType() == IterationRuleType.WEEKLY) {
            return dateCalculator.weeklyTypeDate(date, cycle, rule.daysOfWeekToList());
        }

        if (rule.parseIterationType() == IterationRuleType.MONTHLY) {
            return processCalculatorByMonthlyRule(
                    date, cycle, startDate, monthlyOption, monthlyOption.parseMonthlyMode());
        }

        return dateCalculator.yearlyTypeDate(date, cycle, startDate);
    }

    private LocalDate processCalculatorByMonthlyRule(
            LocalDate date, int cycle, LocalDate startDate, MonthlyOption option, MonthlyMode mode) {
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

    public void createIterationInfo(
            User user,
            AccountBookRequest accountBookRequest,
            AccountBook originalAccountBook,
            List<AccountBook> savedIterationAccountBooks,
            AccountBookType type
    ) {
        IterationInfo iterationInfo = null;
        if (accountBookRequest.parseIterationType() == IterationType.CUSTOM) {
            iterationInfo = getIterationInfo(accountBookRequest.getCustomIteration());
            iterationInfoRepository.save(iterationInfo);
        }

        for (AccountBook savedAccountBook : savedIterationAccountBooks) {
            Iteration iterations
                    = IterationBuilder.buildEntity(user, originalAccountBook, savedAccountBook, iterationInfo, type);
            iterationRepository.save(iterations, type);
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

    public CustomIterationInfoResponse getCustomIteration(Iteration iteration) {
        IterationInfo iterationInfo = iteration.getIterationInfo();
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

    public List<Iteration> getIterationByIterationAction(
            AccountBook accountBook,
            User user,
            IterationAction iterationAction,
            AccountBookType type
    ) {
        Iteration iteration = iterationRepository.findByGeneratedAccountBookAndUser(user, accountBook, type);
        AccountBook originalAccountBook = iteration.getOriginalAccountBook();
        List<Iteration> allIterations
                = iterationRepository.findAllByOriginalAccountBookAndUser(user, originalAccountBook, type);

        return resolveIterations(
                iterationAction, originalAccountBook, accountBook, iteration, allIterations, user, MODIFY_TYPE, type
        );
    }

    public List<AccountBook> deleteIterations(
            AccountBook accountBookToDelete,
            User user,
            IterationAction iterationAction,
            AccountBookType type
    ) {
        Iteration iteration = iterationRepository.findByGeneratedAccountBookAndUser(user, accountBookToDelete, type);
        AccountBook originalAccountBook = iteration.getOriginalAccountBook();
        List<Iteration> allIterations
                = iterationRepository.findAllByOriginalAccountBookAndUser(user, originalAccountBook, type);

        List<Iteration> deleteIterations = resolveIterations(
                iterationAction, originalAccountBook, accountBookToDelete, iteration, allIterations, user, DELETE_TYPE, type
        );

        iterationRepository.deleteAll(deleteIterations, type);
        return deleteIterations.stream()
                .map(Iteration::getGeneratedAccountBook)
                .toList();
    }

    private List<Iteration> resolveIterations(
            IterationAction iterationAction,
            AccountBook originalAccountBook,
            AccountBook accountBook,
            Iteration iteration,
            List<Iteration> allIterations,
            User user,
            String type,
            AccountBookType accountBookType
    ) {
        if (iterationAction == IterationAction.THIS_ONLY) {
            return handleThisOnly(originalAccountBook, accountBook, iteration, allIterations);
        }

        if (iterationAction == IterationAction.ALL
                || (iterationAction == IterationAction.THIS_AND_FUTURE && accountBook.equals(originalAccountBook))) {
            if (type.equals(MODIFY_TYPE)) {
                return allIterations;
            }
            return handleAll(iteration, allIterations);
        }

        if (iterationAction == IterationAction.THIS_AND_FUTURE && !accountBook.equals(originalAccountBook)) {
            return handleThisAndFuture(originalAccountBook, accountBook, user, accountBookType);
        }

        throw new BadRequestException(AccountBookResponse.ITERATION_ACTION_INVALID);
    }

    private List<Iteration> handleThisOnly(
            AccountBook originalAccountBook,
            AccountBook accountBookToDelete,
            Iteration iteration,
            List<Iteration> allIterations
    ) {
        if (accountBookToDelete.equals(originalAccountBook) && allIterations.size() < 2) {
            return handleAll(iteration, allIterations);
        }

        if (accountBookToDelete.equals(originalAccountBook)) {
            updateOriginalAccountBook(allIterations);
        }

        return List.of(iteration);
    }

    private void updateOriginalAccountBook(List<Iteration> allIterations) {
        AccountBook newOriginalAccountBook = allIterations.get(1).getGeneratedAccountBook();
        for (Iteration iteration : allIterations) {
            iteration.updateOriginalAccountBook(newOriginalAccountBook);
        }
    }

    private List<Iteration> handleAll(Iteration iteration, List<Iteration> deleteIterations) {
        IterationInfo iterationInfo = iteration.getIterationInfo();
        if (iterationInfo != null) {
            iterationInfoRepository.delete(deleteIterations.getFirst().getIterationInfo());
        }

        return deleteIterations;
    }

    private List<Iteration> handleThisAndFuture(
            AccountBook originalAccountBook,
            AccountBook targetAccountBook,
            User user,
            AccountBookType type
    ) {
        return iterationRepository
                .getThisAndFutureIterations(originalAccountBook, user, targetAccountBook.getAccountBookDate(), type);
    }

    public List<Long> getIterationAccountBookIds(User user, AccountBookType type) {
        return iterationRepository.originalAccountBookIds(user, type);
    }
}
