package com.poortorich.chart.service;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.util.AccountBookCostExtractor;
import com.poortorich.category.entity.Category;
import com.poortorich.chart.response.AccountBookBarResponse;
import com.poortorich.chart.response.CategoryChart;
import com.poortorich.chart.response.CategoryLineResponse;
import com.poortorich.chart.response.CategoryLog;
import com.poortorich.chart.response.CategoryVerticalResponse;
import com.poortorich.chart.response.PeriodTotal;
import com.poortorich.chart.response.TotalAmountAndSavingResponse;
import com.poortorich.chart.response.TransactionRecord;
import com.poortorich.chart.util.AccountBookUtil;
import com.poortorich.chart.util.AmountFormatter;
import com.poortorich.chart.util.PeriodFormatter;
import com.poortorich.global.date.domain.DateInfo;
import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.domain.WeekInformation;
import com.poortorich.global.date.domain.YearInformation;
import com.poortorich.global.date.response.enums.DateResponse;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.statistics.util.StatCalculator;
import java.math.BigDecimal;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartService {

    public TotalAmountAndSavingResponse getTotalAmountAndSavings(
            List<AccountBook> accountBooks,
            List<AccountBook> savingAccountBooks,
            Category savingCategory
    ) {
        long totalExpense = StatCalculator.calculateSum(
                AccountBookCostExtractor.extractExcludingCategory(accountBooks, savingCategory)
        ).longValue();

        long totalSavings = StatCalculator.calculateSum(
                AccountBookCostExtractor.extract(savingAccountBooks)
        ).longValue();

        return TotalAmountAndSavingResponse.builder()
                .savingCategoryId(savingCategory.getId())
                .totalAmount(totalExpense)
                .totalSaving(totalSavings)
                .build();
    }

    public List<CategoryLog> getCategoryLogs(List<AccountBook> accountBooks, Direction direction) {
        return AccountBookUtil.groupAccountBooksByDate(accountBooks, direction)
                .stream()
                .map(accountBooksOnDate -> {
                    List<TransactionRecord> transactions = AccountBookUtil.mapToTransactionRecord(
                            accountBooksOnDate);

                    return CategoryLog.builder()
                            .date(accountBooksOnDate.getFirst().getAccountBookDate().toString())
                            .countOfTransactions((long) transactions.size())
                            .transactions(transactions)
                            .build();
                })
                .toList();
    }

    public List<CategoryChart> getCategoryChart(List<AccountBook> accountBooks) {
        return AccountBookUtil.mapToCategoryCharts(AccountBookUtil.groupAccountBooksByCategory(accountBooks));
    }

    public AccountBookBarResponse getAccountBookBar(
            List<DateInfo> dateInfos,
            List<List<AccountBook>> accountBooksGroupByDateInfo
    ) {
        List<Long> totalAmounts = accountBooksGroupByDateInfo.stream()
                .map(AccountBookCostExtractor::extract)
                .map(StatCalculator::calculateSum)
                .map(BigDecimal::longValue)
                .toList();

        List<PeriodTotal> periodTotals = new ArrayList<>();
        for (int i = 0; i < accountBooksGroupByDateInfo.size(); i++) {
            DateInfo dateInfo = dateInfos.get(i);

            periodTotals.add(PeriodTotal.builder()
                    .period(switch (dateInfo) {
                                case YearInformation yearInfo -> yearInfo.getYear().toString();
                                case MonthInformation monthInfo -> monthInfo.getYearMonth().toString();
                                default -> throw new BadRequestException(DateResponse.UNSUPPORTED_DATE_FORMAT);
                            }
                    )
                    .totalAmount(totalAmounts.get(i))
                    .label(AmountFormatter.convertAmount(totalAmounts.get(i)))
                    .build());
        }
        return AccountBookBarResponse.builder()
                .differenceAmount(AmountFormatter.compareAmount(totalAmounts.getLast(),
                        totalAmounts.get(totalAmounts.size() - 2)))
                .averageAmount(AmountFormatter.convertAmount(StatCalculator.calculateAverage(totalAmounts).longValue()))
                .totalAmounts(periodTotals)
                .build();
    }

    public CategoryLineResponse getCategoryLine(MonthInformation monthInfo, List<AccountBook> accountBooks,
                                                List<List<AccountBook>> weeklyAccountBooks) {
        List<WeekInformation> weekInfos = monthInfo.getWeeks();
        List<PeriodTotal> periodTotals = new ArrayList<>();
        for (int i = 0; i < weekInfos.size(); i++) {
            WeekInformation weekInfo = weekInfos.get(i);
            String period = PeriodFormatter.formatMonthDayRange(weekInfo.getStartDate(), weekInfo.getEndDate());
            Long totalAmount = StatCalculator.calculateSum(AccountBookCostExtractor.extract(weeklyAccountBooks.get(i)))
                    .longValue();

            periodTotals.add(
                    PeriodTotal.builder()
                            .period(period)
                            .totalAmount(totalAmount)
                            .build()
            );
        }

        return CategoryLineResponse.builder()
                .period(PeriodFormatter.formatLocalDateRange(monthInfo.getStartDate(), monthInfo.getEndDate()))
                .totalAmount(StatCalculator.calculateSum(AccountBookCostExtractor.extract(accountBooks)).longValue())
                .weeklyAmounts(periodTotals)
                .build();
    }

    public CategoryVerticalResponse getCategoryVertical(
            YearInformation yearInfo,
            List<AccountBook> accountBooks,
            List<List<AccountBook>> monthlyAccountBooks
    ) {
        List<PeriodTotal> periodTotals = new ArrayList<>();
        Month[] months = Month.values();
        for (int i = 0; i < monthlyAccountBooks.size(); i++) {
            Long totalAmount = StatCalculator.calculateSum(
                            AccountBookCostExtractor.extract(monthlyAccountBooks.get(i)))
                    .longValue();

            periodTotals.add(PeriodTotal.builder()
                    .period(PeriodFormatter.formatMonthKorean(months[i]))
                    .totalAmount(totalAmount)
                    .build());
        }
        return CategoryVerticalResponse.builder()
                .period(PeriodFormatter.formatLocalDateRange(yearInfo.getStartDate(), yearInfo.getEndDate()))
                .totalAmount(StatCalculator.calculateSum(AccountBookCostExtractor.extract(accountBooks)).longValue())
                .monthlyAmounts(periodTotals)
                .build();
    }
}
