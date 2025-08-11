package com.poortorich.transaction.service;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.response.AccountBookInfoResponse;
import com.poortorich.accountbook.util.AccountBookBuilder;
import com.poortorich.accountbook.util.AccountBookCalculator;
import com.poortorich.accountbook.util.AccountBookExtractor;
import com.poortorich.accountbook.util.AccountBookGrouper;
import com.poortorich.chart.util.PeriodFormatter;
import com.poortorich.transaction.response.DailyDetailsResponse;
import com.poortorich.transaction.response.DailyFinance;
import com.poortorich.transaction.response.DailyTransaction;
import com.poortorich.transaction.response.Logs;
import com.poortorich.transaction.response.WeeklyDetailsResponse;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    public DailyDetailsResponse getDailyDetailsReport(List<AccountBook> dailyIncomes, List<AccountBook> dailyExpenses) {
        Long totalIncome = AccountBookCalculator.sum(dailyIncomes);
        Long totalExpense = AccountBookCalculator.sum(dailyExpenses);

        List<AccountBookInfoResponse> dailyDetails = mergeAccountBook(dailyIncomes, dailyExpenses).stream()
                .map(AccountBookBuilder::buildAccountBookInfoResponse)
                .toList();

        return DailyDetailsResponse.builder()
                .totalAmount(totalIncome - totalExpense)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .dailyDetails(dailyDetails)
                .build();
    }

    public List<AccountBook> mergeAccountBook(List<AccountBook> incomes, List<AccountBook> expenses) {
        return Stream.concat(incomes.stream(), expenses.stream())
                .sorted(
                        Comparator.comparing(AccountBook::getAccountBookDate)
                                .thenComparing(AccountBook::getCreatedDate)
                )
                .toList();
    }

    public List<AccountBook> mergeAccountBookLimit(List<AccountBook> incomes, List<AccountBook> expenses,
                                                   int pageSize) {
        return Stream.concat(incomes.stream(), expenses.stream())
                .sorted(
                        Comparator.comparing(AccountBook::getAccountBookDate)
                                .thenComparing(AccountBook::getCreatedDate)
                )
                .limit(pageSize)
                .toList();
    }

    public List<AccountBook> mergeAccountBookDistinct(List<AccountBook> accountBooks,
                                                      List<AccountBook> mergeAccountBooks) {
        return Stream.concat(accountBooks.stream(), mergeAccountBooks.stream())
                .sorted(
                        Comparator.comparing(AccountBook::getAccountBookDate)
                                .thenComparing(AccountBook::getCreatedDate)
                )
                .distinct()
                .toList();
    }

    public WeeklyDetailsResponse getWeeklyDetailsReport(
            List<AccountBook> weeklyAccountBooks, String period, Long countOfLogs, LocalDate nextCursor, Boolean hasNext
    ) {
        List<DailyTransaction> dailyTransactions = getDailyTransactions(weeklyAccountBooks);
        List<AccountBook> incomes = AccountBookExtractor.extractByType(weeklyAccountBooks, AccountBookType.INCOME);
        List<AccountBook> expenses = AccountBookExtractor.extractByType(weeklyAccountBooks, AccountBookType.EXPENSE);
        Long totalIncome = AccountBookCalculator.sum(incomes);
        Long totalExpense = AccountBookCalculator.sum(expenses);

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
        return AccountBookGrouper.groupByDate(accountBooks).entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<AccountBookInfoResponse> transactions = entry.getValue().stream()
                            .map(AccountBookBuilder::buildAccountBookInfoResponse)
                            .toList();

                    return DailyTransaction.builder()
                            .date(PeriodFormatter.formatYearMonth(date))
                            .countOfTransactions((long) transactions.size())
                            .transactions(transactions)
                            .build();
                })
                .toList();
    }

    public List<DailyFinance> getDailyFinance(List<AccountBook> monthlyIncomes, List<AccountBook> monthlyExpenses) {
        List<AccountBook> monthlyAccountBooks = mergeAccountBook(monthlyIncomes, monthlyExpenses);
        return AccountBookGrouper.groupByDate(monthlyAccountBooks).entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<AccountBook> incomes =
                            AccountBookExtractor.extractByType(entry.getValue(), AccountBookType.INCOME);
                    List<AccountBook> expenses =
                            AccountBookExtractor.extractByType(entry.getValue(), AccountBookType.EXPENSE);

                    return DailyFinance.builder()
                            .date(date.toString())
                            .incomeAmount(AccountBookCalculator.sum(incomes))
                            .expenseAmount(AccountBookCalculator.sum(expenses))
                            .build();
                })
                .toList();
    }

    public Logs getLogs(
            LocalDate startDate, LocalDate endDate, List<AccountBook> monthlyIncomes, List<AccountBook> monthlyExpenses
    ) {
        Long totalIncome = AccountBookCalculator.sum(monthlyIncomes);
        Long totalExpense = AccountBookCalculator.sum(monthlyExpenses);

        return Logs.builder()
                .period(PeriodFormatter.formatMonthDayRange(startDate, endDate))
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalAmount(totalIncome - totalExpense)
                .build();
    }
}
