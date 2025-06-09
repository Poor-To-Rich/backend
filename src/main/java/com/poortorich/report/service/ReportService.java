package com.poortorich.report.service;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.response.AccountBookInfoResponse;
import com.poortorich.accountbook.util.AccountBookBuilder;
import com.poortorich.accountbook.util.AccountBookCostExtractor;
import com.poortorich.chart.util.AccountBookUtil;
import com.poortorich.global.date.constants.DatePattern;
import com.poortorich.global.statistics.util.StatCalculator;
import com.poortorich.report.response.DailyDetailsResponse;
import com.poortorich.report.response.DailyFinance;
import com.poortorich.report.response.DailyTransaction;
import com.poortorich.report.response.Logs;
import com.poortorich.report.response.WeeklyDetailsResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ReportService {

    public DailyDetailsResponse getDailyDetailsReport(List<AccountBook> dailyIncomes, List<AccountBook> dailyExpenses) {
        Long totalIncome = StatCalculator.calculateSum(AccountBookCostExtractor.extract(dailyIncomes)).longValue();
        Long totalExpense = StatCalculator.calculateSum(AccountBookCostExtractor.extract(dailyExpenses)).longValue();

        List<AccountBookInfoResponse> dailyDetails = mergeAccountBook(dailyIncomes, dailyExpenses).stream()
                .map(accountBook ->
                        AccountBookBuilder.buildAccountBookInfoResponse(accountBook, inferAccountBookType(accountBook))
                )
                .toList();

        return DailyDetailsResponse.builder()
                .totalAmount(totalIncome - totalExpense)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .dailyDetails(dailyDetails)
                .build();
    }

    public List<AccountBook> mergeAccountBook(List<AccountBook> incomes, List<AccountBook> expenses) {
        return  Stream.concat(incomes.stream(), expenses.stream())
                .sorted(
                        Comparator.comparing(AccountBook::getAccountBookDate)
                        .thenComparing(AccountBook::getCreatedDate)
                )
                .toList();
    }

    public List<AccountBook> mergeAccountBookLimit(List<AccountBook> incomes, List<AccountBook> expenses, int pageSize) {
        return Stream.concat(incomes.stream(), expenses.stream())
                .sorted(
                        Comparator.comparing(AccountBook::getAccountBookDate)
                                .thenComparing(AccountBook::getCreatedDate)
                )
                .limit(pageSize)
                .toList();
    }

    public List<AccountBook> mergeAccountBookDistinct(List<AccountBook> accountBooks, List<AccountBook> mergeAccountBooks) {
        return Stream.concat(accountBooks.stream(), mergeAccountBooks.stream())
                .sorted(
                        Comparator.comparing(AccountBook::getAccountBookDate)
                        .thenComparing(AccountBook::getCreatedDate)
                )
                .distinct()
                .toList();
    }

    public WeeklyDetailsResponse getWeeklyDetailsReport(
            List<AccountBook> weeklyAccountBooks, String period, LocalDate nextCursor, Boolean hasNext
    ) {
        List<DailyTransaction> dailyTransactions = getDailyTransactions(weeklyAccountBooks);

        Long totalIncome = 0L;
        Long totalExpense = 0L;
        Long countOfLogs = 0L;
        for(AccountBook accountBook : weeklyAccountBooks) {
            if (inferAccountBookType(accountBook) == AccountBookType.EXPENSE) {
                totalExpense += accountBook.getCost();
            }
            else {
                totalIncome += accountBook.getCost();
            }
            countOfLogs++;
        }

        return WeeklyDetailsResponse.builder()
                .period(period)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalAmount(totalIncome - totalExpense)
                .countOfLogs(countOfLogs)
                .hasNext(hasNext)
                .nextCursor(nextCursor.toString())
                .dailyDetails(dailyTransactions)
                .build();
    }

    private List<DailyTransaction> getDailyTransactions(List<AccountBook> accountBooks) {
        return AccountBookUtil.groupAccountBooksByDate(accountBooks)
                .stream()
                .map(accountBookOnDate -> {
                    List<AccountBookInfoResponse> transactions = new ArrayList<>();
                    for (AccountBook accountBook : accountBookOnDate) {
                        AccountBookType type = inferAccountBookType(accountBook);
                        transactions.add(AccountBookBuilder.buildAccountBookInfoResponse(accountBook, type));
                    }

                    return DailyTransaction.builder()
                            .date(accountBookOnDate.getFirst().getAccountBookDate()
                                    .format(DateTimeFormatter.ofPattern(DatePattern.MONTH_DAY_DOT_PATTERN))
                            )
                            .countOfTransactions((long) transactions.size())
                            .transactions(transactions)
                            .build();
                })
                .toList();
    }

    public List<DailyFinance> getDailyFinance(List<AccountBook> monthlyIncomes, List<AccountBook> monthlyExpenses) {
        List<AccountBook> monthlyAccountBooks = mergeAccountBook(monthlyIncomes, monthlyExpenses);
        return AccountBookUtil.groupAccountBooksByDate(monthlyAccountBooks)
                .stream()
                .map(accountBooks -> {
                    Long incomeAmount = 0L;
                    Long expenseAmount = 0L;
                    for (AccountBook accountBook : accountBooks) {
                        if (inferAccountBookType(accountBook) == AccountBookType.EXPENSE) {
                            expenseAmount += accountBook.getCost();
                        }
                        else {
                            incomeAmount += accountBook.getCost();
                        }
                    }

                    return DailyFinance.builder()
                            .date(accountBooks.getFirst().getAccountBookDate().toString())
                            .incomeAmount(incomeAmount)
                            .expenseAmount(expenseAmount)
                            .build();
                })
                .toList();
    }

    public Logs getLogs(
            LocalDate startDate, LocalDate endDate, List<AccountBook> monthlyIncomes, List<AccountBook> monthlyExpenses
    ) {
        Long totalIncome = StatCalculator.calculateSum(AccountBookCostExtractor.extract(monthlyIncomes)).longValue();
        Long totalExpense = StatCalculator.calculateSum(AccountBookCostExtractor.extract(monthlyExpenses)).longValue();

        String startDateString = startDate.format(DateTimeFormatter.ofPattern(DatePattern.MONTH_DAY_DOT_PATTERN));
        String endDateString = endDate.format(DateTimeFormatter.ofPattern(DatePattern.MONTH_DAY_DOT_PATTERN));
        String period = startDateString + "~" + endDateString;

        return Logs.builder()
                .period(period)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalAmount(totalIncome - totalExpense)
                .build();
    }

    private AccountBookType inferAccountBookType(AccountBook accountBook) {
        if (accountBook.getPaymentMethod() != null) {
            return AccountBookType.EXPENSE;
        }

        return  AccountBookType.INCOME;
    }
}
