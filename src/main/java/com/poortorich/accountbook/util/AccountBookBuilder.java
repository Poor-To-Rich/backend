package com.poortorich.accountbook.util;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.request.AccountBookRequest;
import com.poortorich.accountbook.response.AccountBookInfoResponse;
import com.poortorich.accountbook.response.InfoResponse;
import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.expense.response.ExpenseInfoResponse;
import com.poortorich.income.entity.Income;
import com.poortorich.income.request.IncomeRequest;
import com.poortorich.income.response.IncomeInfoResponse;
import com.poortorich.iteration.response.CustomIterationInfoResponse;
import com.poortorich.user.entity.User;
import java.time.LocalDate;

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

    public static AccountBook buildEntity(
            User user,
            LocalDate date,
            AccountBook originalAccountBook,
            AccountBookType type
    ) {
        return switch (type) {
            case EXPENSE -> AccountBookBuilder.buildIterationExpense(user, date, (Expense) originalAccountBook);
            case INCOME -> AccountBookBuilder.buildIterationIncome(user, date, (Income) originalAccountBook);
        };
    }

    private static AccountBook buildIterationExpense(User user, LocalDate date, Expense originalExpense) {
        return Expense.builder()
                .expenseDate(date)
                .title(originalExpense.getTitle())
                .cost(originalExpense.getCost())
                .paymentMethod(originalExpense.getPaymentMethod())
                .memo(originalExpense.getMemo())
                .iterationType(originalExpense.getIterationType())
                .category(originalExpense.getCategory())
                .user(user)
                .build();
    }

    private static AccountBook buildIterationIncome(User user, LocalDate date, Income originalIncome) {
        return Income.builder()
                .incomeDate(date)
                .title(originalIncome.getTitle())
                .cost(originalIncome.getCost())
                .memo(originalIncome.getMemo())
                .iterationType(originalIncome.getIterationType())
                .category(originalIncome.getCategory())
                .user(user)
                .build();
    }

    public static InfoResponse buildInfoResponse(AccountBook accountBook, CustomIterationInfoResponse customIteration,
                                                 AccountBookType type) {
        return switch (type) {
            case EXPENSE -> AccountBookBuilder.buildExpenseInfoResponse((Expense) accountBook, customIteration);
            case INCOME -> AccountBookBuilder.buildIncomeInfoResponse((Income) accountBook, customIteration);
        };
    }

    private static InfoResponse buildExpenseInfoResponse(Expense expense, CustomIterationInfoResponse customIteration) {
        return ExpenseInfoResponse.builder()
                .date(expense.getAccountBookDate())
                .categoryName(expense.getCategory().getName())
                .title(expense.getTitle())
                .cost(expense.getCost())
                .paymentMethod(expense.getPaymentMethod().toString())
                .memo(expense.getMemo())
                .iterationType(expense.getIterationType().toString())
                .customIteration(customIteration)
                .build();
    }

    private static InfoResponse buildIncomeInfoResponse(Income income, CustomIterationInfoResponse customIteration) {
        return IncomeInfoResponse.builder()
                .date(income.getAccountBookDate())
                .categoryName(income.getCategory().getName())
                .title(income.getTitle())
                .cost(income.getCost())
                .memo(income.getMemo())
                .iterationType(income.getIterationType().toString())
                .customIteration(customIteration)
                .build();
    }

    public static AccountBookInfoResponse buildAccountBookInfoResponse(AccountBook accountBook) {
        return AccountBookInfoResponse.builder()
                .id(accountBook.getId())
                .categoryName(accountBook.getCategory().getName())
                .color(accountBook.getCategory().getColor())
                .title(accountBook.getTitle())
                .date(accountBook.getAccountBookDate().toString())
                .isIteration(accountBook.getIterationType().isIteration())
                .type(accountBook.getType().toString())
                .cost(accountBook.getCost())
                .build();
    }
}
