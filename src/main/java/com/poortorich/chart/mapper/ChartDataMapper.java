package com.poortorich.chart.mapper;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.util.AccountBookCalculator;
import com.poortorich.accountbook.util.AccountBookGrouper;
import com.poortorich.category.entity.Category;
import com.poortorich.chart.response.CategoryChart;
import com.poortorich.chart.response.CategoryLog;
import com.poortorich.chart.response.PeriodTotal;
import com.poortorich.chart.response.TransactionRecord;
import com.poortorich.chart.util.AmountFormatter;
import com.poortorich.chart.util.PeriodFormatter;
import com.poortorich.global.date.domain.DateInfo;
import io.jsonwebtoken.lang.Objects;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
public class ChartDataMapper {

    public List<TransactionRecord> mapToTransactionRecords(List<AccountBook> accountBooks) {
        return accountBooks.stream()
                .map(accountBook -> TransactionRecord.builder()
                        .id(accountBook.getId())
                        .title(accountBook.getTitle())
                        .amount(accountBook.getCost())
                        .build())
                .toList();
    }

    public List<CategoryChart> mapToCategoryCharts(Map<Category, List<AccountBook>> accountBooksGroupByCategory) {
        if (Objects.isEmpty(accountBooksGroupByCategory)) {
            return Collections.emptyList();
        }

        Map<Category, Double> rateByCategory = AccountBookCalculator.percentages(accountBooksGroupByCategory);

        return accountBooksGroupByCategory.entrySet().stream()
                .map(entry -> {
                    Category category = entry.getKey();
                    List<AccountBook> accountBooks = entry.getValue();

                    return CategoryChart.builder()
                            .id(category.getId())
                            .color(category.getColor())
                            .name(category.getName())
                            .rate(rateByCategory.get(category))
                            .amount(AccountBookCalculator.sum(accountBooks))
                            .build();
                })
                .toList();
    }

    public List<PeriodTotal> mapToPeriodTotals(Map<DateInfo, List<AccountBook>> accountBookGroupByDateInfo) {
        return accountBookGroupByDateInfo.entrySet().stream()
                .map(entry -> {
                    DateInfo dateInfo = entry.getKey();
                    Long total = AccountBookCalculator.sum(entry.getValue());
                    return PeriodTotal.builder()
                            .period(PeriodFormatter.formatByDateInfo(dateInfo))
                            .totalAmount(total)
                            .label(AmountFormatter.convertAmount(total))
                            .build();
                })
                .toList();
    }

    public List<CategoryLog> mapToCategoryLogs(List<AccountBook> accountBooks, Direction direction) {
        return AccountBookGrouper.groupByDate(accountBooks, direction).entrySet().stream()
                .map(entry ->
                        CategoryLog.builder()
                                .date(entry.getKey().toString())
                                .countOfTransactions((long) entry.getValue().size())
                                .transactions(mapToTransactionRecords(entry.getValue()))
                                .build()
                )
                .toList();
    }
}
