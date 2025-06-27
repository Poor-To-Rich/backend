package com.poortorich.chart.util;

import com.poortorich.accountbook.entity.AccountBook;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountBookUtil {

    public static List<List<AccountBook>> groupAccountBooksByDate(List<AccountBook> accountBooks) {
        return accountBooks.stream()
                .collect(Collectors.groupingBy(AccountBook::getAccountBookDate))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();
    }
}
