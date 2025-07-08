package com.poortorich.iteration.service;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.request.AccountBookRequest;
import com.poortorich.accountbook.response.enums.AccountBookResponse;
import com.poortorich.accountbook.util.AccountBookBuilder;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.accountbook.request.enums.IterationAction;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.iteration.entity.enums.IterationRuleType;
import com.poortorich.iteration.entity.info.IterationInfo;
import com.poortorich.iteration.mapper.IterationMapper;
import com.poortorich.iteration.provider.IterationCalculatorProvider;
import com.poortorich.iteration.repository.IterationInfoRepository;
import com.poortorich.iteration.repository.IterationRepository;
import com.poortorich.iteration.request.CustomIteration;
import com.poortorich.iteration.request.IterationRule;
import com.poortorich.iteration.response.CustomIterationInfoResponse;
import com.poortorich.iteration.response.enums.IterationResponse;
import com.poortorich.iteration.util.IterationBuilder;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IterationService {

    private static final String MODIFY_TYPE = "modify";
    private static final String DELETE_TYPE = "delete";

    private final IterationCalculatorProvider calculatorProvider;
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
        LocalDate endDate = calculatorProvider.calculateEndDateByType(customIteration, accountBook, startDate);
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
            return calculatorProvider.calculateDateByRuleType(
                    customIteration.getCycle(),
                    date, startDate,
                    rule,
                    rule.getMonthlyOption()
            );
        }

        return calculatorProvider.defaultCalculateDateByType(type, date, startDate);
    }

    private int getAllowedIterations(IterationType type, CustomIteration customIteration) {
        if (type == IterationType.CUSTOM) {
            return customIteration.getIterationRule().parseIterationType().maxIterations;
        }

        return IterationRuleType.DAILY.maxIterations;
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
            return IterationBuilder.buildDailyIterationInfo(customIteration);
        }
        if (ruleType == IterationRuleType.WEEKLY) {
            return IterationBuilder.buildWeeklyIterationInfo(customIteration);
        }
        if (ruleType == IterationRuleType.MONTHLY) {
            return IterationBuilder.buildMonthlyIterationInfo(
                    customIteration,
                    customIteration.getIterationRule().getMonthlyOption()
            );
        }
        if (ruleType == IterationRuleType.YEARLY) {
            return IterationBuilder.buildYearlyIterationInfo(customIteration);
        }

        throw new BadRequestException(IterationResponse.ITERATION_RULE_TYPE_INVALID);
    }

    public CustomIterationInfoResponse getCustomIteration(Iteration iteration) {
        IterationInfo iterationInfo = iteration.getIterationInfo();
        return CustomIterationInfoResponse.builder()
                .iterationRule(IterationMapper.toIterationRuleInfo(iterationInfo))
                .cycle(iterationInfo.getCycle())
                .end(IterationMapper.toEndInfo(iterationInfo))
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
                iterationAction,
                originalAccountBook,
                accountBookToDelete,
                iteration,
                allIterations,
                user,
                DELETE_TYPE,
                type
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
