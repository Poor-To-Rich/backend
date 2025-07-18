package com.poortorich.income.repository;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.category.entity.Category;
import com.poortorich.income.entity.Income;
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
public interface IncomeRepository extends JpaRepository<Income, Long> {

    Optional<AccountBook> findByUserAndId(User user, Long id);

    List<Income> findByUserAndIncomeDateBetween(User user, LocalDate startDate, LocalDate endDate);

    List<Income> findByUserAndCategoryAndIncomeDateBetween(User user, Category category, LocalDate startDate,
                                                           LocalDate endDate);

    List<Income> findByUserAndCategoryAndIncomeDate(User user, Category category, LocalDate incomeDate);

    @Query("""
            SELECT i
            FROM Income i
            WHERE i.category = :category
            AND i.user = :user
            AND i.incomeDate >= :startDate
            AND i.incomeDate <= :endDate
            AND i.incomeDate >= :cursor
            ORDER BY i.incomeDate ASC
            """)
    Slice<Income> findIncomeByUserAndCategoryWithinDateRangeWithCursorAsc(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("cursor") LocalDate cursor,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
            SELECT i
            FROM Income i
            WHERE i.category = :category
            AND i.user = :user
            AND i.incomeDate >= :startDate
            AND i.incomeDate <= :endDate
            AND i.incomeDate <= :cursor
            ORDER BY i.incomeDate Desc
            """)
    Slice<Income> findIncomeByUserAndCategoryWithinDateRangeWithCursorDesc(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("cursor") LocalDate cursor,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
            SELECT i
            FROM Income i
            WHERE i.user = :user
              AND i.incomeDate >= :startDate
              AND i.incomeDate <= :endDate
              AND i.incomeDate >= :cursor
            ORDER BY i.incomeDate ASC
            """)
    Slice<Income> findIncomeByUserWithinDateRangeWithCursorAsc(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("cursor") LocalDate cursor,
            Pageable pageable
    );

    @Query("""
            SELECT i
            FROM Income i
            WHERE i.user = :user
              AND i.incomeDate >= :startDate
              AND i.incomeDate <= :endDate
              AND i.incomeDate <= :cursor
            ORDER BY i.incomeDate Desc
            """)
    Slice<Income> findIncomeByUserWithinDateRangeWithCursorDesc(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("cursor") LocalDate cursor,
            Pageable pageable
    );

    List<Income> findByUserAndIncomeDate(User user, LocalDate incomeDate);

    @Query("""
            SELECT COUNT(i)
            FROM Income i
            WHERE i.category = :category
            AND i.user = :user
            AND i.incomeDate >= :startDate
            AND i.incomeDate <= :endDate
            """)
    Long countByUserAndCategoryBetweenDates(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT COUNT(i)
            FROM Income i
            WHERE i.user = :user
              AND i.incomeDate >= :startDate
              AND i.incomeDate <= :endDate
            """)
    Long countByUserAndBetweenDates(User user, LocalDate startDate, LocalDate endDate);

    void deleteByUser(User user);

    List<Income> findByUserAndCategory(User user, Category category);
}
