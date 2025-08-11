package com.poortorich.transaction.facade;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.accountbook.util.AccountBookCalculator;
import com.poortorich.chart.util.PeriodFormatter;
import com.poortorich.global.date.constants.DateConstants;
import com.poortorich.global.date.constants.DatePattern;
import com.poortorich.global.date.domain.MonthInformation;
import com.poortorich.global.date.domain.WeekInformation;
import com.poortorich.global.date.domain.YearInformation;
import com.poortorich.global.date.util.DateInfoProvider;
import com.poortorich.global.date.util.DateParser;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.transaction.response.DailyDetailsResponse;
import com.poortorich.transaction.response.Logs;
import com.poortorich.transaction.response.MonthlyTotalReportResponse;
import com.poortorich.transaction.response.MonthlyTotalResponse;
import com.poortorich.transaction.response.WeeklyDetailsResponse;
import com.poortorich.transaction.response.WeeklyTotalReportResponse;
import com.poortorich.transaction.response.enums.TransactionResponse;
import com.poortorich.transaction.service.TransactionService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionFacade {

    private final UserService userService;
    private final TransactionService transactionService;
    private final AccountBookService accountBookService;

    public DailyDetailsResponse getDailyDetailsReport(String username, String inputDate) {
        User user = userService.findUserByUsername(username);
        LocalDate date = DateParser.parseDate(inputDate);

        List<AccountBook> dailyIncomes
                = accountBookService.getAccountBookBetweenDates(user, date, date, AccountBookType.INCOME);
        List<AccountBook> dailyExpenses
                = accountBookService.getAccountBookBetweenDates(user, date, date, AccountBookType.EXPENSE);

        return transactionService.getDailyDetailsReport(dailyIncomes, dailyExpenses);
    }

    public WeeklyDetailsResponse getWeeklyDetailsReport(String username, String date, Long week, String cursor) {
        User user = userService.findUserByUsername(username);
        MonthInformation monthInfo = (MonthInformation) DateInfoProvider.get(date);
        if (monthInfo.getWeeks().size() < week) {
            throw new BadRequestException(TransactionResponse.WEEK_INVALID);
        }

        LocalDate startDate = monthInfo.getWeeks().get((int) (week - 1)).getStartDate();
        LocalDate endDate = monthInfo.getWeeks().get((int) (week - 1)).getEndDate();
        LocalDate dateCursor = (Objects.isNull(cursor) ? startDate : LocalDate.parse(cursor));
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
                = transactionService.mergeAccountBookLimit(weeklyIncomes.getContent(), weeklyExpenses.getContent(), 20);
        List<AccountBook> accountBooksByLastDate
                = getAccountBooksByLastDate(user, mergeSliceAccountBooks, weeklyExpenses, weeklyIncomes);

        List<AccountBook> weeklyAccountBooks
                = transactionService.mergeAccountBookDistinct(mergeSliceAccountBooks, accountBooksByLastDate);

        LocalDate nextCursor = getNextCursor(accountBooksByLastDate, endDate);
        Boolean hasNext = accountBookService.hasNextPage(user, nextCursor, endDate);
        Long countOfLogs = accountBookService.countByUserAndBetweenDates(user, startDate, endDate);

        return transactionService.getWeeklyDetailsReport(
                weeklyAccountBooks, PeriodFormatter.formatWeeklyReportRange(startDate, endDate), countOfLogs,
                nextCursor, hasNext
        );
    }

    private List<AccountBook> getAccountBooksByLastDate(
            User user, List<AccountBook> mergeSliceAccountBooks, Slice<AccountBook> weeklyExpenses,
            Slice<AccountBook> weeklyIncomes
    ) {
        List<AccountBook> expensesByLastDate = List.of();
        List<AccountBook> incomesByLastDate = List.of();

        if (!weeklyExpenses.isEmpty()) {
            expensesByLastDate = accountBookService.getAccountBooksByUserAndDate(
                    user, mergeSliceAccountBooks.getLast().getAccountBookDate(), AccountBookType.EXPENSE
            );
        }

        if (!weeklyIncomes.isEmpty()) {
            incomesByLastDate = accountBookService.getAccountBooksByUserAndDate(
                    user, mergeSliceAccountBooks.getLast().getAccountBookDate(), AccountBookType.INCOME
            );
        }

        return transactionService.mergeAccountBook(incomesByLastDate, expensesByLastDate);
    }

    private LocalDate getNextCursor(List<AccountBook> accountBooksByLastDate, LocalDate endDate) {
        if (accountBooksByLastDate.isEmpty()) {
            return endDate.plusDays(DateConstants.ONE_DAY);
        }

        return accountBooksByLastDate.getFirst().getAccountBookDate().plusDays(DateConstants.ONE_DAY);
    }

    public MonthlyTotalResponse getMonthlyTotal(String username, String date) {
        User user = userService.findUserByUsername(username);
        MonthInformation monthInfo = (MonthInformation) DateInfoProvider.get(date);

        List<AccountBook> monthlyIncomes = accountBookService.getAccountBookBetweenDates(
                user, monthInfo.getStartDate(), monthInfo.getEndDate(), AccountBookType.INCOME
        );

        List<AccountBook> monthlyExpenses = accountBookService.getAccountBookBetweenDates(
                user, monthInfo.getStartDate(), monthInfo.getEndDate(), AccountBookType.EXPENSE
        );

        Long totalIncome = AccountBookCalculator.sum(monthlyIncomes);
        Long totalExpense = AccountBookCalculator.sum(monthlyExpenses);

        return MonthlyTotalResponse.builder()
                .totalAmount(totalIncome - totalExpense)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .transactions(transactionService.getDailyFinance(monthlyIncomes, monthlyExpenses))
                .build();
    }

    public MonthlyTotalReportResponse getMonthlyTotalReport(String username, String date) {
        User user = userService.findUserByUsername(username);
        YearInformation yearInfo;
        if (date == null) {
            MonthInformation monthInfo = (MonthInformation) DateInfoProvider.get(date);
            yearInfo = (YearInformation) DateInfoProvider.get(
                    monthInfo.getStartDate().format(DateTimeFormatter.ofPattern(DatePattern.YEAR_PATTERN))
            );
        } else {
            yearInfo = (YearInformation) DateInfoProvider.get(date);
        }

        List<AccountBook> yearlyIncomes = accountBookService.getAccountBookBetweenDates(
                user, yearInfo.getStartDate(), yearInfo.getEndDate(), AccountBookType.INCOME
        );

        List<AccountBook> yearlyExpenses = accountBookService.getAccountBookBetweenDates(
                user, yearInfo.getStartDate(), yearInfo.getEndDate(), AccountBookType.EXPENSE
        );

        Long totalIncome = AccountBookCalculator.sum(yearlyIncomes);
        Long totalExpense = AccountBookCalculator.sum(yearlyExpenses);

        return MonthlyTotalReportResponse.builder()
                .yearTotalIncome(totalIncome)
                .yearTotalExpense(totalExpense)
                .yearTotalAmount(totalIncome - totalExpense)
                .monthlyLogs(getMonthlyLogs(user, yearInfo))
                .build();
    }

    private List<Logs> getMonthlyLogs(User user, YearInformation yearInfo) {
        List<Logs> logs = new ArrayList<>();
        for (Month month : DateConstants.MONTHS_ORDERED) {
            MonthInformation monthInfo = yearInfo.getMonths().get(month);

            List<AccountBook> monthlyIncomes = accountBookService.getAccountBookBetweenDates(
                    user, monthInfo.getStartDate(), monthInfo.getEndDate(), AccountBookType.INCOME
            );

            List<AccountBook> monthlyExpenses = accountBookService.getAccountBookBetweenDates(
                    user, monthInfo.getStartDate(), monthInfo.getEndDate(), AccountBookType.EXPENSE
            );

            logs.add(transactionService.getLogs(
                    monthInfo.getStartDate(), monthInfo.getEndDate(), monthlyIncomes, monthlyExpenses
            ));
        }

        return logs;
    }

    public WeeklyTotalReportResponse getWeeklyTotalReport(String username, String date) {
        User user = userService.findUserByUsername(username);
        MonthInformation monthInfo = (MonthInformation) DateInfoProvider.get(date);

        return WeeklyTotalReportResponse.builder()
                .weeklyLogs(getWeeklyLogs(user, monthInfo))
                .build();
    }

    private List<Logs> getWeeklyLogs(User user, MonthInformation monthInfo) {
        List<Logs> weeklyLogs = new ArrayList<>();
        for (WeekInformation weekInfo : monthInfo.getWeeks()) {
            List<AccountBook> weeklyIncomes = accountBookService.getAccountBookBetweenDates(
                    user, weekInfo.getStartDate(), weekInfo.getEndDate(), AccountBookType.INCOME
            );

            List<AccountBook> weeklyExpenses = accountBookService.getAccountBookBetweenDates(
                    user, weekInfo.getStartDate(), weekInfo.getEndDate(), AccountBookType.EXPENSE
            );

            weeklyLogs.add(transactionService.getLogs(
                    weekInfo.getStartDate(), weekInfo.getEndDate(), weeklyIncomes, weeklyExpenses
            ));
        }

        return weeklyLogs;
    }
}
