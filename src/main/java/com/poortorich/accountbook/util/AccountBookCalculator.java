package com.poortorich.accountbook.util;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.category.entity.Category;
import io.jsonwebtoken.lang.Objects;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountBookCalculator {

    private static final double PERCENTAGE_OFFSET = 100.0;

    private AccountBookCalculator() {
    }

    public static Long sum(List<AccountBook> accountBooks) {
        if (Objects.isEmpty(accountBooks)) {
            return 0L;
        }

        return accountBooks.stream()
                .mapToLong(AccountBook::getCost)
                .sum();
    }

    public static Double average(List<AccountBook> accountBooks) {
        if (Objects.isEmpty(accountBooks)) {
            return 0.0;
        }

        return accountBooks.stream()
                .mapToDouble(AccountBook::getCost)
                .average()
                .orElse(0.0);
    }

    public static Double average(Collection<List<AccountBook>> accountBooks) {
        if (Objects.isEmpty(accountBooks)) {
            return 0.0;
        }

        return accountBooks.stream()
                .mapToDouble(AccountBookCalculator::sum)
                .average()
                .orElse(0.0);
    }

    public static Map<Category, Double> percentages(
            Map<Category, List<AccountBook>> accountBookGroupByCategory
    ) {
        if (Objects.isEmpty(accountBookGroupByCategory)) {
            return Collections.emptyMap();
        }
        Double total = accountBookGroupByCategory.values().stream()
                .mapToDouble(AccountBookCalculator::sum)
                .sum();

        Map<Category, Double> percentage = accountBookGroupByCategory.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> getRate(total, sum(entry.getValue()).doubleValue()),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return adjustPercentages(percentage);
    }

    private static Map<Category, Double> adjustPercentages(Map<Category, Double> rateByCategory) {
        double totalRate = rateByCategory.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double diff = PERCENTAGE_OFFSET - totalRate;
        if (Math.abs(diff) < 1e-10) {
            return rateByCategory;
        }

        Category maxKey = rateByCategory.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (Objects.isEmpty(maxKey)) {
            rateByCategory.put(maxKey, rateByCategory.get(maxKey) + diff);
        }

        return rateByCategory;
    }

    private static Double getRate(Double total, Double current) {
        return current / total * PERCENTAGE_OFFSET;
    }

}
