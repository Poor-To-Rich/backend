package com.poortorich.iteration.repository;

import com.poortorich.income.entity.Income;
import com.poortorich.iteration.entity.IterationIncomes;
import com.poortorich.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IterationIncomesRepository extends JpaRepository<IterationIncomes, Long> {

    IterationIncomes findByGeneratedIncomeAndUser(Income accountBookToDelete, User user);

    List<IterationIncomes> findAllByOriginalIncomeAndUser(Income originalAccountBook, User user);

    @Query("""
        SELECT DISTINCT ii
        FROM IterationIncomes ii
        JOIN FETCH ii.generatedIncome gi
        WHERE ii.originalIncome = :originalIncome
          AND ii.user = :user
          AND gi.incomeDate >= :startDate
        ORDER BY gi.incomeDate ASC
        """)
    List<IterationIncomes> getThisAndFutureIterationIncomes(
            Income originalIncome,
            User user,
            LocalDate accountBookDate
    );
}
