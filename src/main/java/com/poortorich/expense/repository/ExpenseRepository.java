package com.poortorich.expense.repository;

import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import com.poortorich.ranking.model.UserExpenseAggregate;
import com.poortorich.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByUserAndId(User user, Long id);

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
    Slice<Expense> findExpenseByUserAndCategoryWithinDateRangeWithCursorAsc(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("cursor") LocalDate cursor,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
            SELECT e
            FROM Expense e
            WHERE e.category = :category
            AND e.user = :user
            AND e.expenseDate >= :startDate
            AND e.expenseDate <= :endDate
            AND e.expenseDate <= :cursor
            ORDER BY e.expenseDate DESC
            """)
    Slice<Expense> findExpenseByUserAndCategoryWithinDateRangeWithCursorDesc(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("cursor") LocalDate cursor,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
            SELECT e
            FROM Expense e
            WHERE e.user = :user
              AND e.expenseDate >= :startDate
              AND e.expenseDate <= :endDate
              AND e.expenseDate >= :cursor
            ORDER BY e.expenseDate ASC
            """)
    Slice<Expense> findExpenseByUserWithinDateRangeWithCursorAsc(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("cursor") LocalDate cursor,
            Pageable pageable
    );

    @Query("""
            SELECT e
            FROM Expense e
            WHERE e.user = :user
              AND e.expenseDate >= :startDate
              AND e.expenseDate <= :endDate
              AND e.expenseDate <= :cursor
            ORDER BY e.expenseDate DESC
            """)
    Slice<Expense> findExpenseByUserWithinDateRangeWithCursorDesc(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("cursor") LocalDate cursor,
            Pageable pageable
    );

    List<Expense> findByUserAndExpenseDate(User user, LocalDate expenseDate);

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

    @Query("""
            SELECT COUNT(e)
            FROM Expense e
            WHERE e.user = :user
              AND e.expenseDate >= :startDate
              AND e.expenseDate <= :endDate
            """)
    Long countByUserAndBetweenDates(User user, LocalDate startDate, LocalDate endDate);

    void deleteByUser(User user);

    List<Expense> findByUserAndCategory(User user, Category category);

    @Query("""
            SELECT NEW  com.poortorich.ranking.model.UserExpenseAggregate(
                e.user.id,
                COALESCE(SUM(e.cost), 0L),
                COUNT(DISTINCT e.expenseDate)
            )
            FROM Expense e
            WHERE e.user IN :users
            AND e.expenseDate BETWEEN :startDate AND :endDate
            GROUP BY e.user.id
            """)
    List<UserExpenseAggregate> findExpenseAggregatesByUsersAndDateRange(
            @Param("users") List<User> users,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
