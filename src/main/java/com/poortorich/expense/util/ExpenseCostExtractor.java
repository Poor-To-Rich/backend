package com.poortorich.expense.util;

import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import java.util.List;
import java.util.Objects;

public class ExpenseCostExtractor {

    private ExpenseCostExtractor() {
    }

    public static List<Long> extract(List<Expense> expenses) {
        return expenses.stream()
                .map(Expense::getCost)
                .toList();
    }
    
    public static List<Long> extractByCategory(List<Expense> expenses, Category category) {
        return expenses.stream()
                .filter(expense -> Objects.equals(expense.getCategory(), category))
                .map(Expense::getCost)
                .toList();
    }

    public static List<Long> extractExcludingCategory(List<Expense> expenses, Category excludedCategory) {
        return expenses.stream()
                .filter(expense -> !Objects.equals(expense.getCategory(), excludedCategory))
                .map(Expense::getCost)
                .toList();
    }
}
