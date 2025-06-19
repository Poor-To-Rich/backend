package com.poortorich.chart.facade;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.category.domain.model.enums.DefaultExpenseCategory;
import com.poortorich.category.entity.Category;
import com.poortorich.category.service.CategoryService;
import com.poortorich.chart.constants.ChartConstants;
import com.poortorich.chart.response.AccountBookBarResponse;
import com.poortorich.chart.response.CategoryChart;
import com.poortorich.chart.response.CategoryChartResponse;
import com.poortorich.chart.response.CategoryLineResponse;
import com.poortorich.chart.response.CategoryLog;
import com.poortorich.chart.response.CategorySectionResponse;
import com.poortorich.chart.response.CategoryVerticalResponse;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.chart.service.ChartService;
import com.poortorich.chart.util.AccountBookUtil;
import com.poortorich.global.date.constants.DateConstants;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.domain.YearInformation;
import com.poortorich.global.date.util.DateInfoProvider;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartFacade {

    private final ChartService chartService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final AccountBookService accountBookService;

    public TotalAmountAndSavingResponse getTotalAccountBookAmountAndSaving(String username, String date,
                                                                           AccountBookType type) {
        User user = userService.findUserByUsername(username);
        DateInfo dateInfo = DateInfoProvider.getDateInfo(date);

        Category savingCategory = categoryService.findCategoryByName(
                DefaultExpenseCategory.SAVINGS_INVESTMENT.getName(),
                user
        );

        List<AccountBook> userAccountBooks = accountBookService.getAccountBookBetweenDates(
                user, dateInfo.getStartDate(), dateInfo.getEndDate(), type
        );

        List<AccountBook> savingAccountBooks = accountBookService.getAccountBookByCategoryBetweenDates(
                user, savingCategory, dateInfo.getStartDate(), dateInfo.getEndDate());
        return chartService.getTotalAmountAndSavings(userAccountBooks, savingAccountBooks, savingCategory);
    }

    public CategorySectionResponse getCategorySection(String username, Long categoryId, String date, String cursor,
                                                      String sortDirection) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.getCategoryOrThrow(categoryId, user);
        DateInfo dateInfo = DateInfoProvider.getDateInfo(date);
        Direction direction = (sortDirection.equals("asc") ? Direction.ASC : Direction.DESC);
        LocalDate dateCursor;
        if (Objects.isNull(cursor)) {
            dateCursor = (direction == Direction.ASC) ? dateInfo.getStartDate() : dateInfo.getEndDate();
        } else {
            dateCursor = LocalDate.parse(cursor);
        }

        Pageable pageable = PageRequest.of(0, 20);

        Slice<AccountBook> accountBooks = accountBookService.getAccountBookByUserAndCategoryWithinDateRangeWithCursor(
                user,
                category,
                dateInfo.getStartDate(),
                dateCursor,
                dateInfo.getEndDate(),
                direction,
                pageable
        );

        if (!accountBooks.hasContent()) {
            return CategorySectionResponse.builder()
                    .hasNext(false)
                    .countOfLogs(0L)
                    .categoryLogs(List.of())
                    .build();
        }

        List<AccountBook> accountBooksByLastDate = accountBookService.getAccountBooksByUserAndCategoryAndAccountBookDate(
                user,
                category,
                accountBooks.getContent().getLast().getAccountBookDate()
        );

        List<CategoryLog> categoryLogs = chartService.getCategoryLogs(
                AccountBookUtil.mergeAccountBooksByDate(
                        accountBooks.getContent(),
                        accountBooksByLastDate),
                direction);

        LocalDate nextCursor;
        if (direction == Direction.ASC) {
            nextCursor = accountBooksByLastDate.getFirst().getAccountBookDate().plusDays(DateConstants.ONE_DAY);
        } else {
            nextCursor = accountBooksByLastDate.getFirst().getAccountBookDate().minusDays(DateConstants.ONE_DAY);
        }

        return CategorySectionResponse.builder()
                .hasNext(accountBookService.hasNextPage(
                        user,
                        category,
                        dateInfo.getStartDate(),
                        dateInfo.getEndDate(),
                        nextCursor, direction))
                .nextCursor(nextCursor.toString())
                .countOfLogs((long) categoryLogs.size())
                .categoryLogs(categoryLogs)
                .build();
    }

    public CategoryChartResponse getCategoryChart(String username, String date, AccountBookType accountBookType) {
        User user = userService.findUserByUsername(username);
        DateInfo dateInfo = DateInfoProvider.getDateInfo(date);

        List<AccountBook> accountBooks = accountBookService.getAccountBookBetweenDates(
                user,
                dateInfo.getStartDate(),
                dateInfo.getEndDate(),
                accountBookType);

        List<CategoryChart> categoryCharts = chartService.getCategoryChart(accountBooks).stream()
                .sorted(Comparator.comparing(CategoryChart::getRate).reversed())
                .toList();

        List<Map<String, BigDecimal>> aggregatedData = List.of(categoryCharts.stream()
                .collect(Collectors.toMap(
                        CategoryChart::getName,
                        CategoryChart::getRate,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new)));

        Map<String, String> categoryColors = categoryCharts.stream()
                .collect(Collectors.toMap(CategoryChart::getName,
                        CategoryChart::getColor,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));

        if (categoryCharts.isEmpty()) {
            aggregatedData = List.of(Map.of(ChartConstants.DUMMY_CATEGORY, ChartConstants.DUMMY_RATE));
            categoryColors = Map.of(ChartConstants.DUMMY_CATEGORY, ChartConstants.DUMMY_COLOR);
        }

        return CategoryChartResponse.builder()
                .aggregatedData(aggregatedData)
                .categoryColors(categoryColors)
                .categoryCharts(categoryCharts)
                .build();
    }

    public AccountBookBarResponse getAccountBookBar(String username, String date, AccountBookType type) {
        User user = userService.findUserByUsername(username);
        List<DateInfo> dateInfos = DateInfoProvider.getPreviousDateInfos(date);

        List<List<AccountBook>> accountBooksGroupByDateInfo = dateInfos.stream()
                .map(dateInfo -> accountBookService.getAccountBookBetweenDates(
                        user,
                        dateInfo.getStartDate(),
                        dateInfo.getEndDate(),
                        type))
                .toList();

        return chartService.getAccountBookBar(dateInfos, accountBooksGroupByDateInfo);
    }

    public CategoryLineResponse getCategoryLine(String username, Long categoryId, String date) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.getCategoryOrThrow(categoryId, user);
        MonthInformation monthInfo = (MonthInformation) DateInfoProvider.getDateInfo(date);

        List<AccountBook> accountBooks = accountBookService.getAccountBookByCategoryBetweenDates(user, category,
                monthInfo.getStartDate(), monthInfo.getEndDate());

        List<List<AccountBook>> weeklyAccountBooks = monthInfo.getWeeks().stream()
                .map(weekInfo -> accountBookService.getAccountBookByCategoryBetweenDates(
                        user, category, weekInfo.getStartDate(), weekInfo.getEndDate()
                ))
                .toList();

        return chartService.getCategoryLine(monthInfo, accountBooks, weeklyAccountBooks);
    }

    public CategoryVerticalResponse getCategoryVertical(String username, Long categoryId, String date) {
        if (date == null) {
            date = Year.now().toString();
        }

        User user = userService.findUserByUsername(username);
        Category category = categoryService.getCategoryOrThrow(categoryId, user);
        YearInformation yearInfo = (YearInformation) DateInfoProvider.getDateInfo(date);

        List<AccountBook> accountBooks = accountBookService.getAccountBookByCategoryBetweenDates(user, category,
                yearInfo.getStartDate(), yearInfo.getEndDate());

        List<List<AccountBook>> monthlyAccountBooks = Arrays.stream(Month.values()).sequential()
                .map(month -> yearInfo.getMonths().get(month))
                .map(monthInfo -> accountBookService.getAccountBookByCategoryBetweenDates(
                        user, category, monthInfo.getStartDate(), monthInfo.getEndDate()
                ))
                .toList();
        return chartService.getCategoryVertical(yearInfo, accountBooks, monthlyAccountBooks);
    }
}
