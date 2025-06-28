package com.poortorich.accountbook.constants;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.category.entity.Category;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AccountBookComparator {

    public static final Comparator<AccountBook> BY_ACCOUNT_BOOK_DATE_ASC =
            Comparator.comparing(AccountBook::getAccountBookDate);

    public static final Comparator<Map.Entry<Category, List<AccountBook>>> BY_CATEGORY_TOTAL_COST_ASC =
            Comparator.comparingLong(entry -> entry.getValue().stream()
                    .mapToLong(AccountBook::getCost)
                    .sum());

    public static final Comparator<LocalDate> BY_DATE_ASC = Comparator.naturalOrder();

    public static final Comparator<LocalDate> BY_DATE_DESC = Comparator.reverseOrder();

    public static final Comparator<Map.Entry<LocalDate, List<AccountBook>>> BY_DATE_KEY_ASC =
            Map.Entry.comparingByKey(BY_DATE_ASC);

    public static final Comparator<Map.Entry<LocalDate, List<AccountBook>>> BY_DATE_KEY_DESC =
            Map.Entry.comparingByKey(BY_DATE_DESC);
}
