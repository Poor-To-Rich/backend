package com.poortorich.accountbook.repository;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.util.strategy.AccountBookStrategyFactory;
import com.poortorich.category.entity.Category;
import com.poortorich.user.entity.User;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountBookRepository {

    private final AccountBookStrategyFactory strategyFactory;

    public void save(AccountBook accountBook, AccountBookType type) {
        strategyFactory.getStrategy(type).save(accountBook);
    }

    public List<AccountBook> saveAll(List<AccountBook> accountBooks, AccountBookType type) {
        return strategyFactory.getStrategy(type).saveAll(accountBooks);
    }

    public Optional<AccountBook> findByIdAndUser(Long id, User user, AccountBookType type) {
        return strategyFactory.getStrategy(type).findByUserAndId(user, id);
    }

    public void delete(AccountBook accountBook, AccountBookType type) {
        strategyFactory.getStrategy(type).delete(accountBook);
    }

    public void deleteAll(List<AccountBook> accountBooks, AccountBookType type) {
        strategyFactory.getStrategy(type).deleteAll(accountBooks);
    }

    public boolean findByUserAndCategory(User user, Category category, AccountBookType type) {
        return strategyFactory
                .getStrategy(type)
                .findByUserAndCategory(user, category)
                .isEmpty();
    }

    public List<AccountBook> findByUserAndExpenseAndDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            AccountBookType type
    ) {
        return strategyFactory.getStrategy(type).findByUserAndDateBetween(user, startDate, endDate);
    }

    public List<AccountBook> getAccountBookByCategoryBetweenDates(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return strategyFactory.getStrategy(category)
                .findByUserAndCategoryAndDateBetween(user, category, startDate, endDate);
    }

    public Slice<AccountBook> getPageByDate(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate cursor,
            LocalDate endDate,
            Direction direction,
            Pageable pageable
    ) {
        return strategyFactory.getStrategy(category)
                .findByUserAndCategoryWithinDateRangeWithCursor(
                        user,
                        category,
                        startDate,
                        cursor,
                        endDate,
                        direction,
                        pageable
                );
    }

    public Slice<AccountBook> findByUserWithinDateRangeWithCursor(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate cursor,
            Pageable pageable,
            AccountBookType type
    ) {
        return strategyFactory.getStrategy(type)
                .findByUserWithinDateRangeWithCursor(
                        user,
                        startDate,
                        endDate,
                        cursor,
                        pageable
                );
    }

    public List<AccountBook> findByUserAndCategoryAndAccountBookDate(
            User user,
            Category category,
            LocalDate accountBookDate
    ) {
        return strategyFactory.getStrategy(category)
                .findByUserAndCategoryAndDateBetween(user, category, accountBookDate, accountBookDate);
    }

    public List<AccountBook> findByUserAndDate(User user, LocalDate accountBookDate, AccountBookType type) {
        return strategyFactory.getStrategy(type).findByUserAndDate(user, accountBookDate);
    }

    public Long countByUserAndCategoryBetweenDates(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return strategyFactory.getStrategy(category)
                .countByUserAndCategoryBetweenDates(user, category, startDate, endDate);
    }

    public Long countByUserAndBetweenDates(User user, LocalDate startDate, LocalDate endDate) {
        return Arrays.stream(AccountBookType.values())
                .mapToLong(type ->
                        strategyFactory.getStrategy(type).countByUserAndBetweenDates(user, startDate, endDate))
                .sum();
    }

    private List<AccountBook> mapToAccountBooks(List<? extends AccountBook> accountBooks) {
        return accountBooks.stream()
                .map(accountBook -> (AccountBook) accountBook)
                .toList();
    }

    public void deleteAllAccountBooksByUser(User user) {
        for (var type : AccountBookType.values()) {
            strategyFactory.getStrategy(type).deleteByUser(user);
        }
    }
}
