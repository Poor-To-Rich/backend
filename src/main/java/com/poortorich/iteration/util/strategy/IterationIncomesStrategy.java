package com.poortorich.iteration.util.strategy;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.income.entity.Income;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.iteration.entity.IterationIncomes;
import com.poortorich.iteration.repository.IterationIncomesRepository;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IterationIncomesStrategy implements IterationStrategy {

    private final IterationIncomesRepository iterationIncomesRepository;

    @Override
    public void save(Iteration iteration) {
        iterationIncomesRepository.save((IterationIncomes) iteration);
    }

    @Override
    public Iteration findByGeneratedAccountBookAndUser(User user, AccountBook accountBookToDelete) {
        return iterationIncomesRepository.findByGeneratedIncomeAndUser((Income) accountBookToDelete, user);
    }

    @Override
    public List<Iteration> findAllByOriginalAccountBookAndUser(User user, AccountBook originalAccountBook) {
        return iterationIncomesRepository.findAllByOriginalIncomeAndUser((Income) originalAccountBook, user).stream()
                .map(Iteration.class::cast)
                .toList();
    }

    @Override
    public void deleteAll(List<Iteration> deleteIterations) {
        iterationIncomesRepository.deleteAll(deleteIterations.stream()
                .map(iteration -> (IterationIncomes) iteration)
                .toList());
    }

    @Override
    public void deleteByUser(User user) {
        iterationIncomesRepository.deleteByUser(user);
    }

    @Override
    public List<Iteration> getThisAndFutureIterations(User user, AccountBook originalAccountBook, LocalDate accountBookDate) {
        return iterationIncomesRepository
                .getThisAndFutureIterationIncomes((Income) originalAccountBook, user, accountBookDate).stream()
                .map(Iteration.class::cast)
                .toList();
    }

    @Override
    public List<Long> originalAccountBookIds(User user) {
        return iterationIncomesRepository.getOriginalIncomeIds(user);
    }

    @Override
    public List<Iteration> findAllByUser(User user) {
        return iterationIncomesRepository.findByUser(user).stream()
                .map(Iteration.class::cast)
                .toList();
    }
}
