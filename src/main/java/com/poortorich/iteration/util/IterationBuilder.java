package com.poortorich.iteration.util;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.expense.entity.Expense;
import com.poortorich.income.entity.Income;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.iteration.entity.IterationExpenses;
import com.poortorich.iteration.entity.IterationIncomes;
import com.poortorich.iteration.entity.info.IterationInfo;
import com.poortorich.user.entity.User;

public class IterationBuilder {

    public static Iteration buildEntity(
            User user,
            AccountBook originalAccountBook,
            AccountBook generatedAccountBook,
            IterationInfo iterationInfo,
            AccountBookType type
    ) {
        return switch (type) {
            case EXPENSE -> IterationBuilder
                    .buildIterationExpenses(user, (Expense) originalAccountBook, (Expense) generatedAccountBook, iterationInfo);
            case INCOME -> IterationBuilder
                    .buildIterationIncomes(user, (Income) originalAccountBook, (Income) generatedAccountBook, iterationInfo);
        };
    }

    private static IterationExpenses buildIterationExpenses(
            User user,
            Expense originalExpense,
            Expense generatedExpense,
            IterationInfo iterationInfo
    ) {
        return IterationExpenses.builder()
                .originalExpense(originalExpense)
                .generatedExpense(generatedExpense)
                .iterationInfo(iterationInfo)
                .user(user)
                .build();
    }

    private static IterationIncomes buildIterationIncomes(
            User user,
            Income originalIncome,
            Income generatedIncome,
            IterationInfo iterationInfo
    ) {
        return IterationIncomes.builder()
                .originalIncome(originalIncome)
                .generatedIncome(generatedIncome)
                .iterationInfo(iterationInfo)
                .user(user)
                .build();
    }
}
