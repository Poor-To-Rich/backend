package com.poortorich.chart.util;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.util.AccountBookCostExtractor;
import com.poortorich.category.entity.Category;
import com.poortorich.chart.response.CategoryChart;
import com.poortorich.chart.response.TransactionRecord;
import com.poortorich.global.statistics.util.StatCalculator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransactionUtil {

    public static List<AccountBook> mergeAccountBookByDate(List<? extends AccountBook> baseAccountBook,
                                                           List<? extends AccountBook> additionalAccountBook) {
        return Stream.concat(
                        baseAccountBook.stream(),
                        additionalAccountBook.stream()
                )
                .distinct()
                .sorted(Comparator.comparing(AccountBook::getAccountBookDate))
                .toList();
    }

    public static List<List<AccountBook>> groupAccountBooksByDate(List<AccountBook> accountBooks) {
        return accountBooks.stream()
                .collect(Collectors.groupingBy(AccountBook::getAccountBookDate))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();
    }

    public static List<List<AccountBook>> groupAccountBooksByCategory(List<AccountBook> accountBooks) {
        return accountBooks.stream()
                .collect(Collectors.groupingBy(AccountBook::getCategory))
                .values()
                .stream()
                .toList();
    }

    public static List<TransactionRecord> mapToTransactionRecord(List<AccountBook> accountBooks) {
        return accountBooks.stream()
                .map(accountBook -> TransactionRecord.builder()
                        .id(accountBook.getId())
                        .title(accountBook.getTitle())
                        .amount(accountBook.getCost())
                        .build())
                .toList();
    }

    public static List<CategoryChart> mapToCategoryCharts(List<List<AccountBook>> accountBooksGroupByCategory) {
        List<Long> categoryAmounts = accountBooksGroupByCategory.stream()
                .map(AccountBookCostExtractor::extract)
                .map(StatCalculator::calculateSum)
                .map(BigDecimal::longValue)
                .toList();

        List<BigDecimal> percentages = StatCalculator.calculatePercentages(categoryAmounts);

        List<CategoryChart> categoryCharts = new ArrayList<>();
        for (int i = 0; i < accountBooksGroupByCategory.size(); i++) {
            AccountBook accountBook = accountBooksGroupByCategory.get(i).getFirst();
            Category category = accountBook.getCategory();

            categoryCharts.add(
                    CategoryChart.builder()
                            .id(category.getId())
                            .color(category.getColor())
                            .name(category.getName())
                            .rate(percentages.get(i))
                            .amount(categoryAmounts.get(i))
                            .build()
            );
        }

        return categoryCharts;
    }
}
