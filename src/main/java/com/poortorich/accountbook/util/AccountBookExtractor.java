package com.poortorich.accountbook.util;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.category.entity.Category;
import java.util.List;
import java.util.Objects;

public class AccountBookExtractor {

    private AccountBookExtractor() {
    }

    public static List<AccountBook> extractByCategory(List<AccountBook> accountBooks, Category category) {
        return accountBooks.stream()
                .filter(accountBook -> Objects.equals(accountBook.getCategory(), category))
                .toList();
    }

    public static List<AccountBook> extractExcludingCategory(List<AccountBook> accountBooks, Category category) {
        return accountBooks.stream()
                .filter(accountBook -> !Objects.equals(accountBook.getCategory(), category))
                .toList();
    }

    public static List<AccountBook> extractByType(List<AccountBook> accountBooks, AccountBookType type) {
        return accountBooks.stream()
                .filter(accountBook -> Objects.equals(accountBook.getType(), type))
                .toList();
    }
}
