package com.poortorich.chart.service;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.util.AccountBookCostExtractor;
import com.poortorich.category.entity.Category;
import com.poortorich.chart.response.AccountBookBarResponse;
import com.poortorich.chart.response.CategoryChart;
import com.poortorich.chart.response.CategoryLog;
import com.poortorich.chart.response.PeriodTotal;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.chart.response.TransactionRecord;
import com.poortorich.chart.util.AccountBookUtil;
import com.poortorich.chart.util.AmountFormatter;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.domain.YearInformation;
import com.poortorich.global.date.response.enums.DateResponse;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.statistics.util.StatCalculator;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    public AccountBookBarResponse getAccountBookBar(
            List<DateInfo> dateInfos,
            List<List<AccountBook>> accountBooksGroupByDateInfo
    ) {
        List<Long> totalAmounts = accountBooksGroupByDateInfo.stream()
                .map(AccountBookCostExtractor::extract)
                .map(StatCalculator::calculateSum)
                .map(BigDecimal::longValue)
                .toList();

        List<PeriodTotal> periodTotals = new ArrayList<>();
        for (int i = 0; i < accountBooksGroupByDateInfo.size(); i++) {
            DateInfo dateInfo = dateInfos.get(i);

            periodTotals.add(PeriodTotal.builder()
                    .period(switch (dateInfo) {
                                case YearInformation yearInfo -> yearInfo.getYear().toString();
                                case MonthInformation monthInfo -> monthInfo.getYearMonth().toString();
                                default -> throw new BadRequestException(DateResponse.UNSUPPORTED_DATE_FORMAT);
                            }
                    )
                    .totalAmount(totalAmounts.get(i))
                    .build());
        }
        return AccountBookBarResponse.builder()
                .differenceAmount(AmountFormatter.compareAmount(totalAmounts.getLast(),
                        totalAmounts.get(totalAmounts.size() - 2)))
                .averageAmount(AmountFormatter.convertAmount(StatCalculator.calculateAverage(totalAmounts).longValue()))
                .totalAmounts(periodTotals)
                .build();
    }
}
