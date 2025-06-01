package com.poortorich.chart.facade;

import com.poortorich.category.domain.model.enums.DefaultExpenseCategory;
import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.chart.response.CategoryLog;
import com.poortorich.chart.response.CategorySectionResponse;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.chart.service.ChartService;
import com.poortorich.chart.util.TransactionUtil;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.service.ExpenseService;
import com.poortorich.global.date.constants.DateConstants;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.global.date.util.DateInfoProvider;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartFacade {

    private final ChartService chartService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ExpenseService expenseService;

    public TotalAmountAndSavingResponse getTotalExpenseAmountAndSaving(String username, String date) {
        User user = userService.findUserByUsername(username);
        DateInfo dateInfo = DateInfoProvider.getDateInfo(date);

        List<Expense> userExpenses = expenseService.getExpensesBetweenDates(
                user, dateInfo.getStartDate(), dateInfo.getEndDate()
        );

        Category savingCategory = categoryService.findCategoryByName(
                DefaultExpenseCategory.SAVINGS_INVESTMENT.getName(),
                user
        );

        return chartService.getTotalExpenseAndSavings(userExpenses, savingCategory);
    }

    public CategorySectionResponse getCategorySection(String username, Long categoryId, String date, String cursor) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.getCategoryOrThrow(categoryId, user);
        DateInfo dateInfo = DateInfoProvider.getDateInfo(date);
        LocalDate dateCursor = (Objects.isNull(cursor) ? dateInfo.getStartDate() : LocalDate.parse(cursor));
        Pageable pageable = PageRequest.of(0, 20);

        Slice<Expense> expenses = expenseService.getExpenseByUserAndCategoryWithinDateRangeWithCursor(
                user,
                category,
                dateInfo.getStartDate(),
                dateCursor,
                dateInfo.getEndDate(),
                pageable
        );

        List<Expense> expensesByLastDate = expenseService.getExpenseByUserAndCategoryAndExpenseDate(
                user,
                category,
                expenses.getContent().getLast().getExpenseDate()
        );

        List<CategoryLog> categoryLogs = chartService.getCategoryLogs(
                TransactionUtil.mergeExpensesByDate(
                        expenses.getContent(),
                        expensesByLastDate));

        LocalDate nextCursor = expensesByLastDate.getFirst().getExpenseDate().plusDays(DateConstants.ONE_DAY);
        return CategorySectionResponse.builder()
                .hasNext(expenseService.hasNextPage(
                        user,
                        category,
                        nextCursor,
                        dateInfo.getEndDate()))
                .nextCursor(nextCursor.toString())
                .countOfLogs((long) categoryLogs.size())
                .categoryLogs(categoryLogs)
                .build();
    }
}
