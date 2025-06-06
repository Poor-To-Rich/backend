package com.poortorich.report.facade;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.accountbook.util.AccountBookCostExtractor;
import com.poortorich.global.date.constants.DateConstants;
import com.poortorich.global.date.constants.DatePattern;
import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.util.DateInfoProvider;
import com.poortorich.global.date.util.DateParser;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.statistics.util.StatCalculator;
import com.poortorich.report.response.DailyDetailsResponse;
import com.poortorich.report.response.MonthlyTotalResponse;
import com.poortorich.report.response.WeeklyDetailsResponse;
import com.poortorich.report.response.enums.ReportResponse;
import com.poortorich.report.service.ReportService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReportFacade {

    private final UserService userService;
    private final ReportService reportService;
    private final AccountBookService accountBookService;

    public DailyDetailsResponse getDailyDetailsReport(String username, String inputDate) {
        User user = userService.findUserByUsername(username);
        LocalDate date = DateParser.parseDate(inputDate);

        List<AccountBook> dailyIncomes
                = accountBookService.getAccountBookBetweenDates(user, date, date, AccountBookType.INCOME);
        List<AccountBook> dailyExpenses
                = accountBookService.getAccountBookBetweenDates(user, date, date, AccountBookType.EXPENSE);

        return reportService.getDailyDetailsReport(dailyIncomes, dailyExpenses);
    }

    public WeeklyDetailsResponse getWeeklyDetailsReport(String username, String date, Long week, String cursor) {
        User user = userService.findUserByUsername(username);
        MonthInformation monthInfo = (MonthInformation) DateInfoProvider.getDateInfo(date);
        if (monthInfo.getWeeks().size() < week) {
            throw new BadRequestException(ReportResponse.WEEK_INVALID);
        }

        LocalDate startDate = monthInfo.getWeeks().get((int) (week - 1)).getStartDate();
        LocalDate endDate = monthInfo.getWeeks().get((int) (week - 1)).getEndDate();
        LocalDate dateCursor = (Objects.isNull(cursor) ?  startDate : LocalDate.parse(cursor));
        Pageable pageable = PageRequest.of(0, 30);

        return getWeeklyDetailsResponse(user, startDate, endDate, dateCursor, pageable);
    }

    private WeeklyDetailsResponse getWeeklyDetailsResponse(
            User user, LocalDate startDate, LocalDate endDate, LocalDate dateCursor, Pageable pageable
    ) {
        Slice<AccountBook> weeklyExpenses = accountBookService.getAccountBookByUserWithinDateRangeWithCursor(
                user, startDate, endDate, dateCursor, pageable, AccountBookType.EXPENSE
        );

        Slice<AccountBook> weeklyIncomes = accountBookService.getAccountBookByUserWithinDateRangeWithCursor(
                user, startDate, endDate, dateCursor, pageable, AccountBookType.INCOME
        );

        List<AccountBook> mergeSliceAccountBooks
                = reportService.mergeAccountBookLimit(weeklyIncomes.getContent(), weeklyExpenses.getContent(), 20);

        List<AccountBook> expensesByLastDate = accountBookService.getAccountBooksByUserAndDate(
                user, mergeSliceAccountBooks.getLast().getAccountBookDate(), AccountBookType.EXPENSE
        );

        List<AccountBook> incomesByLastDate = accountBookService.getAccountBooksByUserAndDate(
                user, mergeSliceAccountBooks.getLast().getAccountBookDate(), AccountBookType.INCOME
        );

        List<AccountBook> accountBooksByLastDate = reportService.mergeAccountBook(incomesByLastDate, expensesByLastDate);

        List<AccountBook> weeklyAccountBooks = reportService.mergeAccountBookDistinct(
                mergeSliceAccountBooks, accountBooksByLastDate
        );

        String startDateString = startDate.format(DateTimeFormatter.ofPattern(DatePattern.LOCAL_DATE_DOT_PATTERN));
        String endDateString = endDate.format(DateTimeFormatter.ofPattern(DatePattern.LOCAL_DATE_DOT_PATTERN));
        String period = startDateString + " - " + endDateString;

        LocalDate nextCursor = accountBooksByLastDate.getFirst().getAccountBookDate().plusDays(DateConstants.ONE_DAY);
        Boolean hasNext = accountBookService.hasNextPage(user, nextCursor, endDate);

        return reportService.getWeeklyDetailsReport(weeklyAccountBooks, period, nextCursor, hasNext);
    }

    public MonthlyTotalResponse getMonthlyTotalReport(String username, String date) {
        User user = userService.findUserByUsername(username);
        MonthInformation monthInfo = (MonthInformation) DateInfoProvider.getDateInfo(date);

        List<AccountBook> monthlyIncomes = accountBookService.getAccountBookBetweenDates(
                user, monthInfo.getStartDate(), monthInfo.getEndDate(), AccountBookType.INCOME
        );

        List<AccountBook> monthlyExpenses = accountBookService.getAccountBookBetweenDates(
                user, monthInfo.getStartDate(), monthInfo.getEndDate(), AccountBookType.EXPENSE
        );

        Long totalIncome = StatCalculator.calculateSum(AccountBookCostExtractor.extract(monthlyIncomes)).longValue();
        Long totalExpense = StatCalculator.calculateSum(AccountBookCostExtractor.extract(monthlyExpenses)).longValue();

        return MonthlyTotalResponse.builder()
                .totalAmount(totalIncome - totalExpense)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .transactions(reportService.getDailyFinance(monthlyIncomes, monthlyExpenses))
                .build();
    }
}
