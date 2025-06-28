package com.poortorich.chart.collector;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.category.domain.model.enums.DefaultExpenseCategory;
import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.chart.model.domain.ChartDataContext;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.domain.YearInformation;
import com.poortorich.global.date.util.DateInfoProvider;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChartDataCollector {

    private final UserService userService;
    private final CategoryService categoryService;
    private final AccountBookService accountBookService;

    public ChartDataContext collectContext(String username, String date) {
        User user = userService.findUserByUsername(username);
        DateInfo dateInfo = DateInfoProvider.get(date);

        return ChartDataContext.builder()
                .user(user)
                .dateInfo(dateInfo)
                .build();
    }

    public ChartDataContext collectCategoryContext(String username, Long categoryId, String date) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.getCategoryOrThrow(categoryId, user);
        DateInfo dateInfo = DateInfoProvider.get(date);

        return ChartDataContext.builder()
                .user(user)
                .dateInfo(dateInfo)
                .category(category)
                .build();
    }

    public Category getSavingCategory(User user) {
        return categoryService.findCategoryByName(DefaultExpenseCategory.SAVINGS_INVESTMENT.getName(), user);
    }

    public List<AccountBook> getAccountBooksByDate(User user, DateInfo dateInfo, AccountBookType type) {
        return accountBookService.getAccountBookBetweenDates(user, dateInfo, type);
    }

    public List<AccountBook> getAccountBooksByCategory(User user, Category category, DateInfo dateInfo) {
        return accountBookService.getAccountBookByCategoryBetweenDates(user, category, dateInfo);
    }

    public List<AccountBook> getAccountBooksByCategory(
            User user, Category category, LocalDate startDate, LocalDate endDate
    ) {
        return accountBookService.getAccountBookByCategoryBetweenDates(user, category, startDate, endDate);
    }

    public List<AccountBook> getPagedAccountBooks(
            User user, Category category, String cursor, DateInfo dateInfo, Direction direction
    ) {
        return accountBookService.getPageByDate(user, category, cursor, dateInfo, direction);
    }

    public Map<DateInfo, List<AccountBook>> getAccountBooksGroupByDateInfo(
            User user, List<DateInfo> dateInfos, AccountBookType type
    ) {
        return dateInfos.stream()
                .collect(Collectors.toMap(
                        dateInfo -> dateInfo,
                        dateInfo -> accountBookService.getAccountBookBetweenDates(user, dateInfo, type),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public Map<DateInfo, List<AccountBook>> getWeeklyAccountBooks(
            User user, Category category, MonthInformation monthInfo
    ) {
        return monthInfo.getWeeks().stream()
                .collect(Collectors.toMap(
                        weekInfo -> weekInfo,
                        weekInfo -> accountBookService.getAccountBookByCategoryBetweenDates(user, category, weekInfo),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public Map<DateInfo, List<AccountBook>> getMonthlyAccountBooks(
            User user, Category category, YearInformation yearInfo
    ) {
        return Arrays.stream(Month.values()).sequential()
                .map(month -> yearInfo.getMonths().get(month))
                .collect(Collectors.toMap(
                        monthInfo -> monthInfo,
                        monthInfo -> accountBookService.getAccountBookByCategoryBetweenDates(user, category, monthInfo),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public Long getTotalCostExcludingCategory(
            User user, DateInfo dateInfo, Category savingCategory, AccountBookType type
    ) {
        return accountBookService.getTotalCostExcludingCategory(user, dateInfo, savingCategory, type);
    }

    public Long getTotalCostByCategory(User user, DateInfo dateInfo, Category savingCategory, AccountBookType type) {
        return accountBookService.getTotalCostByCategory(user, dateInfo, savingCategory, type);
    }
}
