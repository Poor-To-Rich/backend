package com.poortorich.chart.service;

import com.poortorich.category.entity.Category;
import com.poortorich.chart.response.CategoryLog;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.chart.response.TransactionRecord;
import com.poortorich.chart.util.TransactionUtil;
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

    public List<CategoryLog> getCategoryLogs(List<Expense> expenses) {
        return TransactionUtil.groupExpensesByDate(expenses)
                .stream()
                .map(expensesOnDate -> {
                    List<TransactionRecord> transactions = TransactionUtil.mapToTransactionRecord(expensesOnDate);

                    return CategoryLog.builder()
                            .date(expensesOnDate.getFirst().getExpenseDate().toString())
                            .countOfTransactions((long) transactions.size())
                            .transactions(transactions)
                            .build();
                })
                .toList();
    }
}
