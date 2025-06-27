package com.poortorich.chart.service;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.util.AccountBookGrouper;
import com.poortorich.category.entity.Category;
import com.poortorich.chart.aggregator.ChartDataAggregator;
import com.poortorich.chart.factory.ChartResponseFactory;
import com.poortorich.chart.mapper.ChartDataMapper;
import com.poortorich.chart.response.AccountBookBarResponse;
import com.poortorich.chart.response.CategoryChart;
import com.poortorich.chart.response.CategoryLineResponse;
import com.poortorich.chart.response.CategoryLog;
import com.poortorich.chart.response.CategoryVerticalResponse;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.domain.YearInformation;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartService {

    private final ChartDataMapper dataMapper;
    private final ChartDataAggregator dataAggregator;
    private final ChartResponseFactory responseFactory;

    public TotalAmountAndSavingResponse getTotalAmountAndSavings(
            Long totalCost, Long totalSaving, Long savingCategoryId
    ) {
        return responseFactory.createTotalAmountAndSavingResponse(totalCost, totalSaving, savingCategoryId);
    }

    public List<CategoryLog> getCategoryLogs(List<AccountBook> accountBooks, Direction direction) {
        return dataMapper.mapToCategoryLogs(accountBooks, direction);
    }

    public List<CategoryChart> getCategoryChart(List<AccountBook> accountBooks) {
        Map<Category, List<AccountBook>> groupedByCategory =
                AccountBookGrouper.groupByCategory(accountBooks, Direction.ASC);

        return dataMapper.mapToCategoryCharts(groupedByCategory);
    }


    public AccountBookBarResponse getAccountBookBar(
            Map<DateInfo, List<AccountBook>> accountBookGroupByDateInfo
    ) {
        return responseFactory.createAccountBookBarResponse(accountBookGroupByDateInfo);
    }

    public CategoryLineResponse getCategoryLine(
            MonthInformation monthInfo,
            List<AccountBook> accountBooks,
            Map<DateInfo, List<AccountBook>> weeklyAccountBooks
    ) {
        return responseFactory.createCategoryLineResponse(monthInfo, accountBooks, weeklyAccountBooks);
    }

    public CategoryVerticalResponse getCategoryVertical(
            YearInformation yearInfo,
            List<AccountBook> accountBooks,
            Map<DateInfo, List<AccountBook>> monthlyAccountBooks
    ) {
        return responseFactory.createCategoryVerticalResponse(yearInfo, accountBooks, monthlyAccountBooks);
    }
}
