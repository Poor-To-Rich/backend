package com.poortorich.accountbook.repository;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.repository.ExpenseRepository;
import com.poortorich.income.entity.Income;
import com.poortorich.income.repository.IncomeRepository;
import com.poortorich.user.entity.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountBookRepository {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public void save(AccountBook accountBook, AccountBookType type) {
        switch (type) {
            case EXPENSE -> expenseRepository.save((Expense) accountBook);
            case INCOME -> incomeRepository.save((Income) accountBook);
        }
    }

    public List<AccountBook> saveAll(List<AccountBook> accountBooks, AccountBookType type) {
        List<? extends AccountBook> savedAccountBooks = switch (type) {
            case EXPENSE -> expenseRepository.saveAll(accountBooks.stream()
                    .map(accountBook -> (Expense) accountBook)
                    .toList());
            case INCOME -> incomeRepository.saveAll(accountBooks.stream()
                    .map(accountBook -> (Income) accountBook)
                    .toList());
        };

        return mapToAccountBooks(savedAccountBooks);
    }

    public Optional<AccountBook> findByIdAndUser(Long id, User user, AccountBookType type) {
        Optional<? extends AccountBook> accountBook = switch (type) {
            case EXPENSE -> expenseRepository.findByIdAndUser(id, user);
            case INCOME -> incomeRepository.findByIdAndUser(id, user);
        };

        return accountBook.map(entity -> (AccountBook) entity);
    }

    public void delete(AccountBook accountBook, AccountBookType type) {
        switch (type) {
            case EXPENSE -> expenseRepository.delete((Expense) accountBook);
            case INCOME -> incomeRepository.delete((Income) accountBook);
        }
    }

    public void deleteAll(List<AccountBook> accountBooks, AccountBookType type) {
        switch (type) {
            case EXPENSE -> expenseRepository.deleteAll(accountBooks.stream()
                    .map(accountBook -> (Expense) accountBook)
                    .toList());
            case INCOME -> incomeRepository.deleteAll(accountBooks.stream()
                    .map(accountBook -> (Income) accountBook)
                    .toList());
        }
    }

    public List<AccountBook> findByUserAndExpenseAndDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            AccountBookType type) {
        List<? extends AccountBook> accountBooks = switch (type) {
            case EXPENSE -> expenseRepository.findByUserAndExpenseDateBetween(user, startDate, endDate);
            case INCOME -> incomeRepository.findByUserAndIncomeDateBetween(user, startDate, endDate);
        };

        return mapToAccountBooks(accountBooks);
    }

    public List<AccountBook> getAccountBookByCategoryBetweenDates(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<? extends AccountBook> accountBooks = switch (category.getType()) {
            case DEFAULT_EXPENSE, CUSTOM_EXPENSE ->
                    expenseRepository.findByUserAndCategoryAndExpenseDateBetween(user, category, startDate, endDate);
            case DEFAULT_INCOME, CUSTOM_INCOME ->
                    incomeRepository.findByUserAndCategoryAndIncomeDateBetween(user, category, startDate, endDate);
        };

        return mapToAccountBooks(accountBooks);
    }

    public Slice<AccountBook> findByUserAndCategoryWithinDateRangeWithCursor(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate cursor,
            LocalDate endDate,
            Pageable pageable
    ) {
        Slice<? extends AccountBook> accountBookPage = switch (category.getType()) {
            case DEFAULT_EXPENSE, CUSTOM_EXPENSE ->
                    expenseRepository.findExpenseByUserAndCategoryWithinDateRangeWithCursor(
                            user, category, startDate, cursor, endDate, pageable
                    );
            case DEFAULT_INCOME, CUSTOM_INCOME -> incomeRepository.findIncomeByUserAndCategoryWithinDateRangeWithCursor(
                    user, category, startDate, cursor, endDate, pageable
            );
        };

        return new SliceImpl<>(
                accountBookPage.stream()
                        .map(accountBook -> (AccountBook) accountBook)
                        .toList(),
                pageable,
                accountBookPage.hasNext());
    }

    public Slice<AccountBook> findByUserWithinDateRangeWithCursor(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate cursor,
            Pageable pageable,
            AccountBookType type
    ) {
        Slice<? extends AccountBook> accountBookPage = switch (type) {
            case EXPENSE
                    -> expenseRepository.findExpenseByUserWithinDateRangeWithCursor(user, startDate, endDate, cursor, pageable);
            case INCOME
                    -> incomeRepository.findIncomeByUserWithinDateRangeWithCursor(user, startDate, endDate, cursor, pageable);
        };

        return new SliceImpl<>(
                accountBookPage.stream()
                        .map(accountBook -> (AccountBook) accountBook)
                        .toList(),
                pageable,
                accountBookPage.hasNext()
        );
    }

    public List<AccountBook> findByUserAndCategoryAndAccountBookDate(
            User user,
            Category category,
            LocalDate accountBookDate) {
        List<? extends AccountBook> accountBooks = switch (category.getType()) {
            case DEFAULT_EXPENSE, CUSTOM_EXPENSE ->
                    expenseRepository.findByUserAndCategoryAndExpenseDate(user, category, accountBookDate);
            case DEFAULT_INCOME, CUSTOM_INCOME ->
                    incomeRepository.findByUserAndCategoryAndIncomeDate(user, category, accountBookDate);
        };

        return mapToAccountBooks(accountBooks);
    }

    public List<AccountBook> findByUserAndDate(User user, LocalDate accountBookDate, AccountBookType type) {
        List<? extends AccountBook> accountBooks = switch (type) {
            case EXPENSE -> expenseRepository.findByUserAndExpenseDate(user, accountBookDate);
            case INCOME -> incomeRepository.findByUserAndIncomeDate(user, accountBookDate);
        };

        return mapToAccountBooks(accountBooks);
    }

    public Long countByUserAndCategoryBetweenDates(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate endDate) {
        return switch (category.getType()) {
            case DEFAULT_EXPENSE, CUSTOM_EXPENSE -> expenseRepository.countByUserAndCategoryBetweenDates(
                    user, category, startDate, endDate
            );
            case DEFAULT_INCOME, CUSTOM_INCOME -> incomeRepository.countByUserAndCategoryBetweenDates(
                    user, category, startDate, endDate
            );
        };
    }

    public Long countByUserAndBetweenDates(User user, LocalDate startDate, LocalDate endDate) {
        Long expenseCount = expenseRepository.countByUserAndBetweenDates(user, startDate, endDate);
        Long incomeCount = incomeRepository.countByUserAndBetweenDates(user, startDate, endDate);

        return expenseCount + incomeCount;
    }

    private List<AccountBook> mapToAccountBooks(List<? extends AccountBook> accountBooks) {
        return accountBooks.stream()
                .map(accountBook -> (AccountBook) accountBook)
                .toList();
    }

    public void deleteAllAccountBooksByUser(User user) {
        expenseRepository.deleteByUser(user);
        incomeRepository.deleteByUser(user);
    }
}
