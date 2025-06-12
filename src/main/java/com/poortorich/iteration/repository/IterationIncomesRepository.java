package com.poortorich.iteration.repository;

import com.poortorich.income.entity.Income;
import com.poortorich.iteration.entity.IterationIncomes;
import com.poortorich.user.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IterationIncomesRepository extends JpaRepository<IterationIncomes, Long> {

    IterationIncomes findByGeneratedIncomeAndUser(Income accountBookToDelete, User user);

    List<IterationIncomes> findAllByOriginalIncomeAndUser(Income originalAccountBook, User user);

    List<IterationIncomes> findByUser(User user);
    
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

    @Query("""
            SELECT DISTINCT ii.originalIncome.id
            FROM IterationIncomes ii
            WHERE ii.user = :user
            """)
    List<Long> getOriginalIncomeIds(User user);

    void deleteByUser(User user);
}
