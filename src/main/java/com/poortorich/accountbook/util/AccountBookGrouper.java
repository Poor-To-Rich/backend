package com.poortorich.accountbook.util;

import com.poortorich.accountbook.constants.AccountBookComparator;
import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.category.entity.Category;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort.Direction;

public class AccountBookGrouper {

    private AccountBookGrouper() {
    }

    public static Map<LocalDate, List<AccountBook>> groupByDate(List<AccountBook> accountBooks) {
        return groupByDate(accountBooks, Direction.ASC);
    }

    public static Map<LocalDate, List<AccountBook>> groupByDate(List<AccountBook> accountBooks, Direction direction) {
        var comparator = switch (direction) {
            case ASC -> AccountBookComparator.BY_DATE_ASC;
            case DESC -> AccountBookComparator.BY_DATE_DESC;
        };

        return accountBooks.stream()
                .collect(Collectors.groupingBy(
                        AccountBook::getAccountBookDate,
                        () -> new TreeMap<>(comparator),
                        Collectors.toList()));
    }

    public static Map<Category, List<AccountBook>> groupByCategory(
            List<AccountBook> accountBooks,
            Direction direction
    ) {
        var comparator = switch (direction) {
            case ASC -> AccountBookComparator.BY_CATEGORY_TOTAL_COST_ASC;
            case DESC -> AccountBookComparator.BY_CATEGORY_TOTAL_COST_ASC.reversed();
        };

        return accountBooks.stream()
                .collect(Collectors.groupingBy(AccountBook::getCategory))
                .entrySet()
                .stream()
                .sorted(comparator)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }
}
