package com.poortorich.expense.repository;

import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import com.poortorich.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByIdAndUser(Long id, User user);

    List<Expense> findByUserAndExpenseDateBetween(User user, LocalDate startDate, LocalDate endDate);

    List<Expense> findByUserAndCategoryAndExpenseDateBetween(User user, Category category, LocalDate startDate,
                                                             LocalDate endDate);

    List<Expense> findByUserAndCategoryAndExpenseDate(User user, Category category, LocalDate expenseDate);

    @Query("""
            SELECT e
            FROM Expense e
            WHERE e.category = :category
            AND e.user = :user
            AND e.expenseDate >= :startDate
            AND e.expenseDate <= :endDate
            AND e.expenseDate >= :cursor
            ORDER BY e.expenseDate ASC
            """
    )
    Slice<Expense> findExpenseByUserAndCategoryWithinDateRangeWithCursor(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("cursor") LocalDate cursor,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(e)
            FROM Expense e
            WHERE e.category = :category
            AND e.user = :user
            AND e.expenseDate >= :startDate
            AND e.expenseDate <= :endDate
            """)
    Long countByUserAndCategoryBetweenDates(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
