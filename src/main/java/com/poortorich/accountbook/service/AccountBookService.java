package com.poortorich.accountbook.service;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.repository.AccountBookRepository;
import com.poortorich.accountbook.request.AccountBookRequest;
import com.poortorich.accountbook.util.AccountBookBuilder;
import com.poortorich.category.entity.Category;
import com.poortorich.expense.response.ExpenseResponse;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountBookService {

    private final AccountBookRepository accountBookRepository;

    public AccountBook create(
            User user,
            Category category,
            AccountBookRequest accountBookRequest,
            AccountBookType type) {
        AccountBook accountBook = AccountBookBuilder.buildEntity(user, category, accountBookRequest, type);
        accountBookRepository.save(accountBook, type);
        return accountBook;
    }

    public List<AccountBook> createAccountBookAll(List<AccountBook> accountBooks, AccountBookType type) {
        return accountBookRepository.saveAll(accountBooks, type);
    }

    public void deleteAccountBook(Long accountBookId, User user, AccountBookType type) {
        AccountBook accountBook = getAccountBookOrThrow(accountBookId, user, type);
        accountBookRepository.delete(accountBook, type);
    }

    public void deleteAccountBookAll(List<AccountBook> accountBooks, AccountBookType type) {
        accountBookRepository.deleteAll(accountBooks, type);
    }

    public List<AccountBook> getAccountBookBetweenDates(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            AccountBookType type) {
        return Optional.of(accountBookRepository.findByUserAndExpenseAndDateBetween(user, startDate, endDate, type))
                .orElseThrow(() -> new NotFoundException(ExpenseResponse.EXPENSE_NON_EXISTENT));
    }

    public Slice<AccountBook> getAccountBookByUserAndCategoryWithinDateRangeWithCursor(
            User user,
            Category category,
            LocalDate startDate,
            LocalDate cursor,
            LocalDate endDate,
            Pageable pageable
    ) {
        return accountBookRepository.findByUserAndCategoryWithinDateRangeWithCursor(
                user,
                category,
                startDate,
                cursor,
                endDate,
                pageable
        );
    }

    public List<AccountBook> getAccountBooksByUserAndCategoryAndAccountBookDate(
            User user,
            Category category,
            LocalDate accountBookDate) {
        return accountBookRepository.findByUserAndCategoryAndAccountBookDate(user, category, accountBookDate);
    }

    public Boolean hasNextPage(User user, Category category, LocalDate startDate, LocalDate endDate) {
        return accountBookRepository.countByUserAndCategoryBetweenDates(user, category, startDate, endDate) > 0L;
    }

    public AccountBook getAccountBookOrThrow(Long id, User user, AccountBookType type) {
        return accountBookRepository.findByIdAndUser(id, user, type)
                .orElseThrow(() -> new NotFoundException(ExpenseResponse.EXPENSE_NON_EXISTENT));
    }
}
