package com.poortorich.expense.util;

import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import java.util.List;
import java.util.Objects;

public class ExpenseExtractor {

    public List<Expense> extractSavingCategory(List<Expense> expenses, Category savingCategory) {
        return expenses.stream()
                .filter(expense -> Objects.equals(expense.getCategory(), savingCategory))
                .map(Expense::getCost)
                .toList();
    }
}
