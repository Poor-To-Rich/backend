package com.poortorich.iteration.repository;

import com.poortorich.expense.entity.Expense;
import com.poortorich.iteration.entity.IterationExpenses;
import com.poortorich.user.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IterationExpensesRepository extends JpaRepository<IterationExpenses, Long> {

    IterationExpenses findByGeneratedExpenseAndUser(Expense targetExpense, User userByUsername);

    List<IterationExpenses> findAllByOriginalExpenseAndUser(Expense origianlExpense, User user);

    @Query("""
            SELECT DISTINCT ie
            FROM IterationExpenses ie
            JOIN FETCH ie.generatedExpense ge
            WHERE ie.originalExpense = :originalExpense
              AND ie.user = :user
              AND ge.expenseDate >= :startDate
            ORDER BY ge.expenseDate ASC
            """)
    List<IterationExpenses> getThisAndFutureIterationExpenses(
            Expense originalExpense,
            User user,
            LocalDate startDate
    );

    @Query("""
            SELECT DISTINCT ie.originalExpense.id
            FROM IterationExpenses ie
            """)
    List<Long> getOriginalExpenseIds();

    void deleteByUser(User user);
}
