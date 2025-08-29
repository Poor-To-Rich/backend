package com.poortorich.accountbook.util.strategy;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.category.entity.Category;
import com.poortorich.ranking.model.UserExpenseAggregate;
import com.poortorich.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AccountBookStrategy {

    void save(AccountBook accountBook);

    List<AccountBook> saveAll(List<AccountBook> accountBooks);

    Optional<AccountBook> findByUserAndId(User user, Long id);

    void delete(AccountBook accountBook);

    void deleteAll(List<AccountBook> accountBooks);

    List<AccountBook> findByUserAndCategory(User user, Category category);

    List<AccountBook> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    List<AccountBook> findByUserAndCategoryAndDateBetween(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate endDate
    );

    List<AccountBook> findByUserAndDate(User user, LocalDate date);

    Long countByUserAndCategoryBetweenDates(User user, Category category, LocalDate startDate, LocalDate endDate);

    Long countByUserAndBetweenDates(User user, LocalDate startDate, LocalDate endDate);

    void deleteByUser(User user);

    Slice<AccountBook> findByUserAndCategoryWithinDateRangeWithCursor(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate cursor,
            LocalDate endDate,
            Direction direction,
            Pageable pageable
    );

    Slice<AccountBook> findByUserWithinDateRangeWithCursor(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate cursor,
            Pageable pageable
    );

    List<UserExpenseAggregate> findExpenseAggregatesByUsersAndDateRange(
            List<User> users,
            LocalDate startDate,
            LocalDate endDate
    );
}
