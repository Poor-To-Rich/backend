package com.poortorich.chart.service;

import com.poortorich.category.entity.Category;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.expense.entity.Expense;
import com.poortorich.expense.util.ExpenseCostExtractor;
import com.poortorich.global.statistics.util.StatCalculator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartService {

    public TotalAmountAndSavingResponse getTotalExpenseAndSavings(List<Expense> expenses, Category savingCategory) {
        long totalExpense = StatCalculator.calculateSum(
                ExpenseCostExtractor.extractExcludingCategory(expenses, savingCategory)
        ).longValue();

        long totalSavings = StatCalculator.calculateSum(
                ExpenseCostExtractor.extractByCategory(expenses, savingCategory)
        ).longValue();

        return TotalAmountAndSavingResponse.builder()
                .savingCategoryId(savingCategory.getId())
                .totalAmount(totalExpense)
                .totalSaving(totalSavings)
                .build();
    }
}
