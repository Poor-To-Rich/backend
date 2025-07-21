package com.poortorich.iteration.util.strategy;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.expense.entity.Expense;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.iteration.entity.IterationExpenses;
import com.poortorich.iteration.repository.IterationExpensesRepository;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IterationExpensesStrategy implements IterationStrategy {

    private final IterationExpensesRepository iterationExpensesRepository;

    @Override
    public void save(Iteration iteration) {
        iterationExpensesRepository.save((IterationExpenses) iteration);
    }

    @Override
    public Iteration findByGeneratedAccountBookAndUser(User user, AccountBook accountBookToDelete) {
        return iterationExpensesRepository.findByGeneratedExpenseAndUser((Expense) accountBookToDelete, user);
    }

    @Override
    public List<Iteration> findAllByOriginalAccountBookAndUser(User user, AccountBook originalAccountBook) {
        return iterationExpensesRepository.findAllByOriginalExpenseAndUser((Expense) originalAccountBook, user).stream()
                .map(Iteration.class::cast)
                .toList();
    }

    @Override
    public void deleteAll(List<Iteration> deleteIterations) {
        iterationExpensesRepository.deleteAll(deleteIterations.stream()
                .map(iteration -> (IterationExpenses) iteration)
                .toList());
    }

    @Override
    public void deleteByUser(User user) {
        iterationExpensesRepository.deleteByUser(user);
    }

    @Override
    public List<Iteration> getThisAndFutureIterations(User user, AccountBook originalAccountBook, LocalDate accountBookDate) {
        return iterationExpensesRepository
                .getThisAndFutureIterationExpenses((Expense) originalAccountBook, user, accountBookDate).stream()
                .map(Iteration.class::cast)
                .toList();
    }

    @Override
    public List<Long> originalAccountBookIds(User user) {
        return iterationExpensesRepository.getOriginalExpenseIds(user);
    }

    @Override
    public List<Iteration> findAllByUser(User user) {
        return iterationExpensesRepository.findByUser(user).stream()
                .map(Iteration.class::cast)
                .toList();
    }
}
