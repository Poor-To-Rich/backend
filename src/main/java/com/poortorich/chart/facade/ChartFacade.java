package com.poortorich.chart.facade;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.category.domain.model.enums.DefaultExpenseCategory;
import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.chart.aggregator.ChartDataAggregator;
import com.poortorich.chart.response.AccountBookBarResponse;
import com.poortorich.chart.response.CategoryChart;
import com.poortorich.chart.response.CategoryChartResponse;
import com.poortorich.chart.response.CategoryLineResponse;
import com.poortorich.chart.response.CategoryLog;
import com.poortorich.chart.response.CategorySectionResponse;
import com.poortorich.chart.response.CategoryVerticalResponse;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.chart.service.ChartService;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.domain.YearInformation;
import com.poortorich.global.date.util.DateInfoProvider;
import com.poortorich.page.domain.Pagination;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartFacade {

    private final ChartService chartService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final AccountBookService accountBookService;

    private final Pagination pageProvider;
    private final ChartDataAggregator dataAggregator;

    public TotalAmountAndSavingResponse getTotalAccountBookAmountAndSaving(
            String username, String date, AccountBookType type
    ) {
        User user = userService.findUserByUsername(username);
        DateInfo dateInfo = DateInfoProvider.get(date);
        Category savingCategory = categoryService.findCategoryByName(
                DefaultExpenseCategory.SAVINGS_INVESTMENT.getName(), user);

        Long totalCost = accountBookService.getTotalCostExcludingCategory(user, dateInfo, savingCategory, type);
        Long totalSaving = accountBookService.getTotalCostByCategory(user, dateInfo, savingCategory, type);

        return chartService.getTotalAmountAndSavings(totalCost, totalSaving, savingCategory.getId());
    }


    public CategorySectionResponse getCategorySection(
            String username, Long categoryId, String date, String cursor, Direction direction
    ) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.getCategoryOrThrow(categoryId, user);
        DateInfo dateInfo = DateInfoProvider.get(date);

        List<AccountBook> accountBooks = accountBookService.getPageByDate(user, category, cursor, dateInfo, direction);
        List<CategoryLog> categoryLogs = chartService.getCategoryLogs(accountBooks, direction);
        LocalDate nextCursor = pageProvider.getNextCursor(accountBooks, direction);
        Boolean hasNext = accountBookService.hasNextPage(
                user, category, dateInfo.getStartDate(), dateInfo.getEndDate(), nextCursor, direction
        );
        return CategorySectionResponse.builder()
                .hasNext(hasNext)
                .nextCursor(nextCursor.toString())
                .countOfLogs(categoryLogs.stream()
                        .mapToLong(CategoryLog::getCountOfTransactions)
                        .sum())
                .categoryLogs(categoryLogs)
                .build();
    }

    public CategoryChartResponse getCategoryChart(String username, String date, AccountBookType accountBookType) {
        User user = userService.findUserByUsername(username);
        DateInfo dateInfo = DateInfoProvider.get(date);
        List<AccountBook> accountBooks = accountBookService.getAccountBookBetweenDates(user, dateInfo, accountBookType);

        List<CategoryChart> categoryCharts = chartService.getCategoryChart(accountBooks);

        return CategoryChartResponse.builder()
                .aggregatedData(List.of(dataAggregator.getAggregatedDataFromCategoryChart(categoryCharts)))
                .categoryColors(dataAggregator.getCategoryColorsFromCategoryChart(categoryCharts))
                .categoryCharts(categoryCharts)
                .build();
    }

    public AccountBookBarResponse getAccountBookBar(String username, String date, AccountBookType type) {
        User user = userService.findUserByUsername(username);
        List<DateInfo> dateInfos = DateInfoProvider.getPreviousAndCurrent(date);

        Map<DateInfo, List<AccountBook>> accountBooksGroupByDateInfo = dateInfos.stream()
                .collect(Collectors.toMap(
                        dateInfo -> dateInfo,
                        dateInfo -> accountBookService.getAccountBookBetweenDates(user, dateInfo, type),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return chartService.getAccountBookBar(accountBooksGroupByDateInfo);
    }

    public CategoryLineResponse getCategoryLine(String username, Long categoryId, String date) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.getCategoryOrThrow(categoryId, user);
        MonthInformation monthInfo = (MonthInformation) DateInfoProvider.get(date);

        List<AccountBook> accountBooks = accountBookService.getAccountBookByCategoryBetweenDates(user, category,
                monthInfo.getStartDate(), monthInfo.getEndDate());

        Map<DateInfo, List<AccountBook>> weeklyAccountBooks = monthInfo.getWeeks().stream()
                .collect(Collectors.toMap(
                        weekInfo -> weekInfo,
                        weekInfo -> accountBookService.getAccountBookByCategoryBetweenDates(
                                user, category, weekInfo),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return chartService.getCategoryLine(monthInfo, accountBooks, weeklyAccountBooks);
    }

    public CategoryVerticalResponse getCategoryVertical(String username, Long categoryId, String date) {
        if (date == null) {
            date = Year.now().toString();
        }

        User user = userService.findUserByUsername(username);
        Category category = categoryService.getCategoryOrThrow(categoryId, user);
        YearInformation yearInfo = (YearInformation) DateInfoProvider.get(date);

        List<AccountBook> accountBooks = accountBookService.getAccountBookByCategoryBetweenDates(user, category,
                yearInfo.getStartDate(), yearInfo.getEndDate());

        Map<DateInfo, List<AccountBook>> monthlyAccountBooks = Arrays.stream(Month.values()).sequential()
                .map(month -> yearInfo.getMonths().get(month))
                .collect(Collectors.toMap(
                        monthInfo -> monthInfo,
                        monthInfo -> accountBookService.getAccountBookByCategoryBetweenDates(
                                user, category, monthInfo
                        ),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return chartService.getCategoryVertical(yearInfo, accountBooks, monthlyAccountBooks);
    }
}
