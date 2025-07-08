package com.poortorich.chart.factory;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.util.AccountBookCalculator;
import com.poortorich.chart.aggregator.ChartDataAggregator;
import com.poortorich.chart.mapper.ChartDataMapper;
import com.poortorich.chart.model.domain.PaginationResult;
import com.poortorich.chart.response.AccountBookBarResponse;
import com.poortorich.chart.response.CategoryChart;
import com.poortorich.chart.response.CategoryChartResponse;
import com.poortorich.chart.response.CategoryLineResponse;
import com.poortorich.chart.response.CategoryLog;
import com.poortorich.chart.response.CategorySectionResponse;
import com.poortorich.chart.response.CategoryVerticalResponse;
import com.poortorich.chart.response.PeriodTotal;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.chart.util.AmountFormatter;
import com.poortorich.chart.util.PeriodFormatter;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.domain.YearInformation;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChartResponseFactory {

    private final ChartDataMapper dataMapper;
    private final ChartDataAggregator dataAggregator;

    public TotalAmountAndSavingResponse createTotalAmountAndSavingResponse(
            Long totalCost, Long totalSaving, Long savingCategoryId
    ) {
        return TotalAmountAndSavingResponse.builder()
                .savingCategoryId(savingCategoryId)
                .totalAmount(totalCost)
                .totalSaving(totalSaving)
                .build();
    }

    public AccountBookBarResponse createAccountBookBarResponse(
            Map<DateInfo, List<AccountBook>> accountBookGroupByDateInfo
    ) {
        long average = dataAggregator.calculateAverage(accountBookGroupByDateInfo);
        String differenceAmount = dataAggregator.calculateDifferenceAmount(accountBookGroupByDateInfo);
        List<PeriodTotal> periodTotals = dataMapper.mapToPeriodTotals(accountBookGroupByDateInfo);

        return AccountBookBarResponse.builder()
                .differenceAmount(differenceAmount)
                .averageAmount(AmountFormatter.convertAmount(average))
                .totalAmounts(periodTotals)
                .build();
    }

    public CategoryLineResponse createCategoryLineResponse(
            MonthInformation monthInfo,
            List<AccountBook> accountBooks,
            Map<DateInfo, List<AccountBook>> weeklyAccountBooks
    ) {
        return CategoryLineResponse.builder()
                .period(PeriodFormatter.formatLocalDateRange(monthInfo.getStartDate(), monthInfo.getEndDate()))
                .totalAmount(AccountBookCalculator.sum(accountBooks))
                .weeklyAmounts(dataMapper.mapToPeriodTotals(weeklyAccountBooks))
                .build();
    }

    public CategoryVerticalResponse createCategoryVerticalResponse(
            YearInformation yearInfo,
            List<AccountBook> accountBooks,
            Map<DateInfo, List<AccountBook>> monthlyAccountBooks
    ) {
        return CategoryVerticalResponse.builder()
                .period(PeriodFormatter.formatLocalDateRange(yearInfo.getStartDate(), yearInfo.getEndDate()))
                .totalAmount(AccountBookCalculator.sum(accountBooks))
                .monthlyAmounts(dataMapper.mapToPeriodTotals(monthlyAccountBooks))
                .build();
    }

    public CategorySectionResponse createCategorySectionResponse(
            List<CategoryLog> categoryLogs,
            Long countOfLog,
            PaginationResult paginationResult
    ) {
        return CategorySectionResponse.builder()
                .hasNext(paginationResult.getHasNext())
                .nextCursor(paginationResult.getNextCursor())
                .countOfLogs(countOfLog)
                .categoryLogs(categoryLogs)
                .build();
    }

    public CategoryChartResponse createCategoryChartResponse(List<CategoryChart> categoryCharts) {
        return CategoryChartResponse.builder()
                .aggregatedData(List.of(dataAggregator.getAggregatedDataFromCategoryChart(categoryCharts)))
                .categoryColors(dataAggregator.getCategoryColorsFromCategoryChart(categoryCharts))
                .categoryCharts(categoryCharts)
                .build();
    }
}
