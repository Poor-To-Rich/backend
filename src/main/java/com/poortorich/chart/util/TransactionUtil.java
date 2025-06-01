package com.poortorich.chart.util;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.chart.response.TransactionRecord;
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

    public static List<TransactionRecord> mapToTransactionRecord(List<AccountBook> accountBooks) {
        return accountBooks.stream()
                .map(accountBook -> TransactionRecord.builder()
                        .id(accountBook.getId())
                        .title(accountBook.getTitle())
                        .amount(accountBook.getCost())
                        .build())
                .toList();
    }
}
