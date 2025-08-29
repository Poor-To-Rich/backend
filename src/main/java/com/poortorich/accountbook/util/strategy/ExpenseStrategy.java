package com.poortorich.accountbook.util.strategy;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.repository.ExpenseRepository;
import com.poortorich.ranking.model.UserExpenseAggregate;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExpenseStrategy implements AccountBookStrategy {

    private final ExpenseRepository expenseRepository;

    @Override
    public void save(AccountBook accountBook) {
        expenseRepository.save((Expense) accountBook);
    }

    @Override
    public List<AccountBook> saveAll(List<AccountBook> accountBooks) {
        List<Expense> expenses = accountBooks.stream()
                .map(Expense.class::cast)
                .toList();

        return expenseRepository.saveAll(expenses).stream()
                .map(AccountBook.class::cast)
                .toList();
    }

    @Override
    public Optional<AccountBook> findByUserAndId(User user, Long id) {
        return expenseRepository.findByUserAndId(user, id)
                .map(AccountBook.class::cast);
    }

    @Override
    public void delete(AccountBook accountBook) {
        expenseRepository.delete((Expense) accountBook);

    }

    @Override
    public void deleteAll(List<AccountBook> accountBooks) {
        List<Expense> expenses = accountBooks.stream()
                .map(Expense.class::cast)
                .toList();
        expenseRepository.deleteAll(expenses);
    }

    @Override
    public List<AccountBook> findByUserAndCategory(User user, Category category) {
        return expenseRepository.findByUserAndCategory(user, category).stream()
                .map(AccountBook.class::cast)
                .toList();
    }

    @Override
    public List<AccountBook> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserAndExpenseDateBetween(user, startDate, endDate).stream()
                .map(AccountBook.class::cast)
                .toList();
    }

    @Override
    public List<AccountBook> findByUserAndCategoryAndDateBetween(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return expenseRepository.findByUserAndCategoryAndExpenseDateBetween(user, category, startDate, endDate).stream()
                .map(AccountBook.class::cast)
                .toList();
    }

    @Override
    public List<AccountBook> findByUserAndDate(User user, LocalDate date) {
        return expenseRepository.findByUserAndExpenseDate(user, date).stream()
                .map(AccountBook.class::cast)
                .toList();
    }

    @Override
    public Long countByUserAndCategoryBetweenDates(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return expenseRepository.countByUserAndCategoryBetweenDates(user, category, startDate, endDate);
    }

    @Override
    public Long countByUserAndBetweenDates(User user, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.countByUserAndBetweenDates(user, startDate, endDate);
    }

    @Override
    public Slice<AccountBook> findByUserAndCategoryWithinDateRangeWithCursor(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate cursor,
            LocalDate endDate,
            Direction direction,
            Pageable pageable
    ) {
        Slice<Expense> expensesPage = switch (direction) {
            case ASC -> expenseRepository.findExpenseByUserAndCategoryWithinDateRangeWithCursorAsc(
                    user,
                    category,
                    startDate,
                    cursor,
                    endDate,
                    pageable
            );
            case DESC -> expenseRepository.findExpenseByUserAndCategoryWithinDateRangeWithCursorDesc(
                    user,
                    category,
                    startDate,
                    cursor,
                    endDate,
                    pageable
            );
        };

        return mapToAccountBookSlice(expensesPage, pageable);
    }

    @Override
    public void deleteByUser(User user) {
        expenseRepository.deleteByUser(user);
    }

    @Override
    public Slice<AccountBook> findByUserWithinDateRangeWithCursor(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate cursor,
            Pageable pageable
    ) {
        Slice<Expense> expensesPage = expenseRepository.findExpenseByUserWithinDateRangeWithCursorAsc(
                user,
                startDate,
                endDate,
                cursor,
                pageable
        );

        return mapToAccountBookSlice(expensesPage, pageable);
    }

    private Slice<AccountBook> mapToAccountBookSlice(Slice<Expense> expensesPage, Pageable pageable) {
        return new SliceImpl<>(
                expensesPage.stream()
                        .map(AccountBook.class::cast)
                        .toList(),
                pageable,
                expensesPage.hasNext()
        );
    }

    @Override
    public List<UserExpenseAggregate> findExpenseAggregatesByUsersAndDateRange(List<User> users, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findExpenseAggregatesByUsersAndDateRange(users, startDate, endDate);
    }
}
