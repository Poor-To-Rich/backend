package com.poortorich.accountbook.util;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.category.entity.Category;
import java.util.List;
import java.util.Objects;

public class AccountBookCostExtractor {

    private AccountBookCostExtractor() {
    }

    public static List<Long> extract(List<AccountBook> accountBooks) {
        return accountBooks.stream()
                .map(AccountBook::getCost)
                .toList();
    }

    public static List<Long> extractByCategory(List<AccountBook> accountBooks, Category category) {
        return accountBooks.stream()
                .filter(accountBook -> Objects.equals(accountBook.getCategory(), category))
                .map(AccountBook::getCost)
                .toList();
    }

    public static List<Long> extractExcludingCategory(List<AccountBook> accountBooks, Category excludedCategory) {
        return accountBooks.stream()
                .filter(accountBook -> !Objects.equals(accountBook.getCategory(), excludedCategory))
                .map(AccountBook::getCost)
                .toList();
    }
}
