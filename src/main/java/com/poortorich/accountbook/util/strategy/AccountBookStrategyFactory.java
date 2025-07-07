package com.poortorich.accountbook.util.strategy;

import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.category.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountBookStrategyFactory {

    private final ExpenseStrategy expenseStrategy;
    private final IncomeStrategy incomeStrategy;

    public AccountBookStrategy getStrategy(AccountBookType type) {
        return switch (type) {
            case EXPENSE -> expenseStrategy;
            case INCOME -> incomeStrategy;
        };
    }

    public AccountBookStrategy getStrategy(Category category) {
        return switch (category.getType()) {
            case DEFAULT_EXPENSE, CUSTOM_EXPENSE -> expenseStrategy;
            case DEFAULT_INCOME, CUSTOM_INCOME -> incomeStrategy;
        };
    }
}
