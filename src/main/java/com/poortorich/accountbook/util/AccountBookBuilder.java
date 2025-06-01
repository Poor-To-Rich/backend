package com.poortorich.accountbook.util;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.request.AccountBookRequest;
import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.income.entity.Income;
import com.poortorich.income.request.IncomeRequest;
import com.poortorich.user.entity.User;

public class AccountBookBuilder {

    public static AccountBook buildEntity(
            User user,
            Category category,
            AccountBookRequest accountBookRequest,
            AccountBookType type) {
        return switch (type) {
            case EXPENSE -> AccountBookBuilder.buildExpense(user, category, (ExpenseRequest) accountBookRequest);
            case INCOME -> AccountBookBuilder.buildIncome(user, category, (IncomeRequest) accountBookRequest);
        };
    }

    private static AccountBook buildExpense(User user, Category category, ExpenseRequest expenseRequest) {
        return Expense.builder()
                .expenseDate(expenseRequest.parseDate())
                .category(category)
                .title(expenseRequest.trimTitle())
                .cost(expenseRequest.getCost())
                .paymentMethod(expenseRequest.parsePaymentMethod())
                .memo(expenseRequest.getMemo())
                .iterationType(expenseRequest.parseIterationType())
                .user(user)
                .build();
    }

    private static AccountBook buildIncome(User user, Category category, IncomeRequest incomeRequest) {
        return Income.builder()
                .incomeDate(incomeRequest.parseDate())
                .category(category)
                .title(incomeRequest.trimTitle())
                .cost(incomeRequest.getCost())
                .memo(incomeRequest.getMemo())
                .iterationType(incomeRequest.parseIterationType())
                .user(user)
                .build();
    }
}
