package com.poortorich.accountbook.util;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.category.entity.Category;
import io.jsonwebtoken.lang.Objects;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountBookCalculator {

    private static final BigDecimal PERCENTAGE_OFFSET = BigDecimal.valueOf(100.0);

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

    public static BigDecimal average(Collection<List<AccountBook>> accountBooks) {
        if (Objects.isEmpty(accountBooks)) {
            return BigDecimal.valueOf(0.0);
        }

        return BigDecimal.valueOf(accountBooks.stream()
                        .mapToDouble(AccountBookCalculator::sum)
                        .average()
                        .orElse(0.0))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static Map<Category, BigDecimal> percentages(
            Map<Category, List<AccountBook>> accountBookGroupByCategory
    ) {
        if (Objects.isEmpty(accountBookGroupByCategory)) {
            return Collections.emptyMap();
        }
        BigDecimal total = BigDecimal.valueOf(accountBookGroupByCategory.values().stream()
                .mapToDouble(AccountBookCalculator::sum)
                .sum());

        Map<Category, BigDecimal> percentage = accountBookGroupByCategory.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> getRate(total, BigDecimal.valueOf(sum(entry.getValue()).doubleValue())),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return adjustPercentages(percentage);
    }

    private static Map<Category, BigDecimal> adjustPercentages(Map<Category, BigDecimal> rateByCategory) {
        BigDecimal totalRate = rateByCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal diff = PERCENTAGE_OFFSET.subtract(totalRate);

        Category maxKey = rateByCategory.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (Objects.isEmpty(maxKey)) {
            rateByCategory.put(maxKey, rateByCategory.get(maxKey).add(diff));
        }

        return rateByCategory;
    }

    private static BigDecimal getRate(BigDecimal total, BigDecimal current) {
        return current.divide(total, 10, RoundingMode.HALF_UP)
                .multiply(PERCENTAGE_OFFSET)
                .setScale(1, RoundingMode.HALF_UP);
    }

}
