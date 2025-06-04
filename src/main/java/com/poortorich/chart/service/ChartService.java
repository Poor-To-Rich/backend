package com.poortorich.chart.service;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.util.AccountBookCostExtractor;
import com.poortorich.category.entity.Category;
import com.poortorich.chart.response.CategoryChart;
import com.poortorich.chart.response.CategoryLog;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.chart.response.TransactionRecord;
import com.poortorich.chart.util.AccountBookUtil;
import com.poortorich.global.statistics.util.StatCalculator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartService {

    public TotalAmountAndSavingResponse getTotalAmountAndSavings(
            List<AccountBook> accountBooks,
            Category savingCategory
    ) {
        long totalExpense = StatCalculator.calculateSum(
                AccountBookCostExtractor.extractExcludingCategory(accountBooks, savingCategory)
        ).longValue();

        long totalSavings = StatCalculator.calculateSum(
                AccountBookCostExtractor.extractByCategory(accountBooks, savingCategory)
        ).longValue();

        return TotalAmountAndSavingResponse.builder()
                .savingCategoryId(savingCategory.getId())
                .totalAmount(totalExpense)
                .totalSaving(totalSavings)
                .build();
    }

    public List<CategoryLog> getCategoryLogs(List<AccountBook> accountBooks) {
        return AccountBookUtil.groupAccountBooksByDate(accountBooks)
                .stream()
                .map(accountBooksOnDate -> {
                    List<TransactionRecord> transactions = AccountBookUtil.mapToTransactionRecord(
                            accountBooksOnDate);

                    return CategoryLog.builder()
                            .date(accountBooksOnDate.getFirst().getAccountBookDate().toString())
                            .countOfTransactions((long) transactions.size())
                            .transactions(transactions)
                            .build();
                })
                .toList();
    }

    public List<CategoryChart> getCategoryChart(List<AccountBook> accountBooks) {
        return AccountBookUtil.mapToCategoryCharts(AccountBookUtil.groupAccountBooksByCategory(accountBooks));
    }
}
