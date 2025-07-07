package com.poortorich.accountbook.util.strategy;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.category.entity.Category;
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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncomeStrategy implements AccountBookStrategy {

    private final IncomeRepository incomeRepository;

    @Override
    public void save(AccountBook accountBook) {
        incomeRepository.save((Income) accountBook);
    }

    @Override
    public List<AccountBook> saveAll(List<AccountBook> accountBooks) {
        List<Income> incomes = accountBooks.stream()
                .map(Income.class::cast)
                .toList();

        return incomeRepository.saveAll(incomes).stream()
                .map(AccountBook.class::cast)
                .toList();
    }

    @Override
    public Optional<AccountBook> findByUserAndId(User user, Long id) {
        return incomeRepository.findByUserAndId(user, id);
    }

    @Override
    public void delete(AccountBook accountBook) {
        incomeRepository.delete((Income) accountBook);
    }

    @Override
    public void deleteAll(List<AccountBook> accountBooks) {
        List<Income> incomes = accountBooks.stream()
                .map(Income.class::cast)
                .toList();

        incomeRepository.deleteAll(incomes);
    }

    @Override
    public List<AccountBook> findByUserAndCategory(User user, Category category) {
        return incomeRepository.findByUserAndCategory(user, category)
                .stream()
                .map(AccountBook.class::cast)
                .toList();
    }

    @Override
    public List<AccountBook> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findByUserAndIncomeDateBetween(user, startDate, endDate)
                .stream()
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
        return incomeRepository.findByUserAndCategoryAndIncomeDateBetween(user, category, startDate, endDate)
                .stream()
                .map(AccountBook.class::cast)
                .toList();
    }

    @Override
    public List<AccountBook> findByUserAndDate(User user, LocalDate date) {
        return incomeRepository.findByUserAndIncomeDate(user, date)
                .stream()
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
        return incomeRepository.countByUserAndCategoryBetweenDates(user, category, startDate, endDate);
    }

    @Override
    public Long countByUserAndBetweenDates(User user, LocalDate startDate, LocalDate endDate) {
        return incomeRepository.countByUserAndBetweenDates(user, startDate, endDate);
    }

    @Override
    public void deleteByUser(User user) {
        incomeRepository.deleteByUser(user);
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
        Slice<Income> incomesPage = switch (direction) {
            case ASC -> incomeRepository.findIncomeByUserAndCategoryWithinDateRangeWithCursorAsc(
                    user,
                    category,
                    startDate,
                    cursor,
                    endDate,
                    pageable
            );
            case DESC -> incomeRepository.findIncomeByUserAndCategoryWithinDateRangeWithCursorDesc(
                    user,
                    category,
                    startDate,
                    cursor,
                    endDate,
                    pageable
            );
        };
        return mapToAccountBookSlice(incomesPage, pageable);
    }

    @Override
    public Slice<AccountBook> findByUserWithinDateRangeWithCursor(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate cursor,
            Pageable pageable
    ) {
        Slice<Income> incomesPage = incomeRepository.findIncomeByUserWithinDateRangeWithCursorAsc(
                user,
                startDate,
                endDate,
                cursor,
                pageable
        );

        return mapToAccountBookSlice(incomesPage, pageable);
    }

    private Slice<AccountBook> mapToAccountBookSlice(Slice<Income> incomesPage, Pageable pageable) {
        return new SliceImpl<>(
                incomesPage.stream()
                        .map(AccountBook.class::cast)
                        .toList(),
                pageable,
                incomesPage.hasNext()
        );
    }
}
