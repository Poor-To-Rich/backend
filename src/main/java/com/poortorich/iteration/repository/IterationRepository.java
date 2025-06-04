package com.poortorich.iteration.repository;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.expense.entity.Expense;
import com.poortorich.income.entity.Income;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.iteration.entity.IterationExpenses;
import com.poortorich.iteration.entity.IterationIncomes;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IterationRepository {

    private final IterationExpensesRepository iterationExpensesRepository;
    private final IterationIncomesRepository iterationIncomesRepository;

    public void save(Iteration iteration, AccountBookType type) {
        switch (type) {
            case EXPENSE -> iterationExpensesRepository.save((IterationExpenses) iteration);
            case INCOME -> iterationIncomesRepository.save((IterationIncomes) iteration);
        }
    }

    public Iteration findByGeneratedAccountBookAndUser(User user, AccountBook accountBookToDelete, AccountBookType type) {
        return switch (type) {
            case EXPENSE -> iterationExpensesRepository.findByGeneratedExpenseAndUser((Expense) accountBookToDelete, user);
            case INCOME -> iterationIncomesRepository.findByGeneratedIncomeAndUser((Income) accountBookToDelete, user);
        };
    }

    public List<Iteration> findAllByOriginalAccountBookAndUser(User user, AccountBook originalAccountBook, AccountBookType type) {
        List<? extends Iteration> allIteration = switch (type) {
            case EXPENSE -> iterationExpensesRepository.findAllByOriginalExpenseAndUser((Expense) originalAccountBook, user);
            case INCOME -> iterationIncomesRepository.findAllByOriginalIncomeAndUser((Income) originalAccountBook, user);
        };
        return mapToIteration(allIteration);
    }

    public void deleteAll(List<Iteration> deleteIterations, AccountBookType type) {
        switch (type) {
            case EXPENSE -> iterationExpensesRepository.deleteAll(deleteIterations.stream()
                    .map(iteration -> (IterationExpenses) iteration)
                    .toList());
            case INCOME -> iterationIncomesRepository.deleteAll(deleteIterations.stream()
                    .map(iteration -> (IterationIncomes) iteration)
                    .toList());
        }
    }

    public List<Iteration> getThisAndFutureIterations(AccountBook originalAccountBook, User user, LocalDate accountBookDate, AccountBookType type) {
        List<? extends Iteration> thisAndFutureIterations = switch (type) {
            case EXPENSE -> iterationExpensesRepository
                    .getThisAndFutureIterationExpenses((Expense) originalAccountBook, user, accountBookDate);
            case INCOME -> iterationIncomesRepository
                    .getThisAndFutureIterationIncomes((Income) originalAccountBook, user, accountBookDate);
        };
        return mapToIteration(thisAndFutureIterations);
    }

    private List<Iteration> mapToIteration(List<? extends Iteration> iterations) {
        return iterations.stream()
                .map(iteration -> (Iteration) iteration)
                .toList();
    }
}
