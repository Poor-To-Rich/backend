package com.poortorich.chart.facade;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.category.entity.Category;
import com.poortorich.chart.collector.ChartDataCollector;
import com.poortorich.chart.factory.ChartResponseFactory;
import com.poortorich.chart.handler.ChartPaginationHandler;
import com.poortorich.chart.model.domain.ChartDataContext;
import com.poortorich.chart.model.domain.PaginationResult;
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
import java.time.Year;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartFacade {

    private final ChartService chartService;
    private final ChartDataCollector dataCollector;
    private final ChartPaginationHandler paginationHandler;
    private final ChartResponseFactory responseFactory;

    public TotalAmountAndSavingResponse getTotalAccountBookAmountAndSaving(
            String username, String date, AccountBookType type
    ) {
        ChartDataContext context = dataCollector.collectContext(username, date);
        Category savingCategory = dataCollector.getSavingCategory(context.getUser());

        Long totalCost = dataCollector.getTotalCostExcludingCategory(
                context.getUser(), context.getDateInfo(), savingCategory, type);
        Long totalSaving = dataCollector.getTotalCostByCategory(
                context.getUser(), context.getDateInfo(), savingCategory, type);

        return chartService.getTotalAmountAndSavings(totalCost, totalSaving, savingCategory.getId());
    }


    public CategorySectionResponse getCategorySection(
            String username, Long categoryId, String date, String cursor, Direction direction
    ) {
        ChartDataContext context = dataCollector.collectCategoryContext(username, categoryId, date);

        List<AccountBook> accountBooks = dataCollector.getPagedAccountBooks(
                context.getUser(), context.getCategory(), cursor, context.getDateInfo(), direction);

        Long countOfLogs = dataCollector.getCountOfLogs(context);

        List<CategoryLog> categoryLogs = chartService.getCategoryLogs(accountBooks, direction);

        PaginationResult paginationResult = paginationHandler.handlePagination(
                context.getUser(), context.getCategory(), context.getDateInfo(), accountBooks, direction);

        return responseFactory.createCategorySectionResponse(categoryLogs, countOfLogs, paginationResult);
    }

    public CategoryChartResponse getCategoryChart(String username, String date, AccountBookType accountBookType) {
        ChartDataContext context = dataCollector.collectContext(username, date);

        List<AccountBook> accountBooks = dataCollector.getAccountBooksByDate(
                context.getUser(), context.getDateInfo(), accountBookType);
        List<CategoryChart> categoryCharts = chartService.getCategoryChart(accountBooks);

        return responseFactory.createCategoryChartResponse(categoryCharts);
    }

    public AccountBookBarResponse getAccountBookBar(String username, String date, AccountBookType type) {
        ChartDataContext context = dataCollector.collectContext(username, date);
        List<DateInfo> dateInfos = DateInfoProvider.getPreviousAndCurrent(date);

        Map<DateInfo, List<AccountBook>> accountBookGroupByDateInfo =
                dataCollector.getAccountBooksGroupByDateInfo(context.getUser(), dateInfos, type);

        return chartService.getAccountBookBar(accountBookGroupByDateInfo);
    }

    public CategoryLineResponse getCategoryLine(String username, Long categoryId, String date) {
        ChartDataContext context = dataCollector.collectCategoryContext(username, categoryId, date);
        MonthInformation monthInfo = (MonthInformation) context.getDateInfo();

        List<AccountBook> accountBooks = dataCollector.getAccountBooksByCategory(
                context.getUser(), context.getCategory(), monthInfo);

        Map<DateInfo, List<AccountBook>> weeklyAccountBooks =
                dataCollector.getWeeklyAccountBooks(context.getUser(), context.getCategory(), monthInfo);

        return chartService.getCategoryLine(monthInfo, accountBooks, weeklyAccountBooks);
    }

    public CategoryVerticalResponse getCategoryVertical(String username, Long categoryId, String date) {
        if (date == null) {
            date = Year.now().toString();
        }

        ChartDataContext context = dataCollector.collectCategoryContext(username, categoryId, date);
        YearInformation yearInfo = (YearInformation) context.getDateInfo();

        List<AccountBook> accountBooks = dataCollector.getAccountBooksByCategory(
                context.getUser(), context.getCategory(), yearInfo);

        Map<DateInfo, List<AccountBook>> monthlyAccountBooks =
                dataCollector.getMonthlyAccountBooks(context.getUser(), context.getCategory(), yearInfo);

        return chartService.getCategoryVertical(yearInfo, accountBooks, monthlyAccountBooks);
    }
}
