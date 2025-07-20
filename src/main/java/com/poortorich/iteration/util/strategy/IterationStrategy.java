package com.poortorich.iteration.util.strategy;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.user.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface IterationStrategy {

    void save(Iteration iteration);

    Iteration findByGeneratedAccountBookAndUser(User user, AccountBook accountBookToDelete);

    List<Iteration> findAllByOriginalAccountBookAndUser(User user, AccountBook originalAccountBook);

    void deleteAll(List<Iteration> deleteIterations);

    void deleteByUser(User user);

    List<Iteration> getThisAndFutureIterations(User user, AccountBook originalAccountBook, LocalDate accountBookDate);

    List<Long> originalAccountBookIds(User user);

    List<Iteration> findAllByUser(User user);
}
