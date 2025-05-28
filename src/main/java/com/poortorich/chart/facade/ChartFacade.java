package com.poortorich.chart.facade;

import com.poortorich.category.domain.model.enums.DefaultExpenseCategory;
import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.chart.service.ChartService;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.service.ExpenseService;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.global.date.util.DateInfoProvider;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
}
