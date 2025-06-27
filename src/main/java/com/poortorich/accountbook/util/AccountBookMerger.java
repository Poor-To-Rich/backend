package com.poortorich.accountbook.util;

import com.poortorich.accountbook.entity.AccountBook;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class AccountBookMerger {

    private AccountBookMerger() {
    }

    public static List<AccountBook> mergeByDate(
            List<? extends AccountBook> base,
            List<? extends AccountBook> addition
    ) {
        return Stream.concat(base.stream(), addition.stream())
                .distinct()
                .sorted(Comparator.comparing(AccountBook::getAccountBookDate))
                .toList();
    }
}
