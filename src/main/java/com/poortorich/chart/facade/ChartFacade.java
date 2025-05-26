package com.poortorich.chart.facade;

import com.poortorich.category.domain.model.enums.DefaultExpenseCategory;
import com.poortorich.category.service.CategoryService;
import com.poortorich.chart.response.TotalExpenseAndSavingResponse;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.service.ExpenseService;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.global.date.util.DateInfoProvider;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartFacade {

    private final UserService userService;
    private final CategoryService categoryService;
    private final ExpenseService expenseService;

    public TotalExpenseAndSavingResponse getTotalExpenseAmountAndSaving(String username, String date) {
        User user = userService.findUserByUsername(username);
        DateInfo dateInfo = DateInfoProvider.getDateInfo(date);

        List<Expense> userExpenses = expenseService.getExpensesBetweenDates(
                user, dateInfo.getStartDate(), dateInfo.getEndDate()
        );
        long savingCategoryId = categoryService.findCategoryByName(
                        DefaultExpenseCategory.SAVINGS_INVESTMENT.name(), user)
                .getId();

        long totalExpenseAmount = 0;
        long totalSavingAmount = 0;
        for (Expense expense : userExpenses) {
            if (Objects.equals(expense.getCategory().getName(), DefaultExpenseCategory.SAVINGS_INVESTMENT.name())) {
                totalSavingAmount += expense.getCost();
                continue;
            }
            totalExpenseAmount += expense.getCost();
        }

        return TotalExpenseAndSavingResponse.builder()
                .savingCategoryId(savingCategoryId)
                .totalExpenseAmount(totalExpenseAmount)
                .totalSavingAmount(totalSavingAmount)
                .build();
    }
}
