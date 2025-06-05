package com.poortorich.report.service;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.response.AccountBookInfoResponse;
import com.poortorich.accountbook.util.AccountBookBuilder;
import com.poortorich.accountbook.util.AccountBookCostExtractor;
import com.poortorich.global.statistics.util.StatCalculator;
import com.poortorich.report.response.DailyDetailsResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ReportService {

    public DailyDetailsResponse getDailyDetailsReport(List<AccountBook> dailyIncomes, List<AccountBook> dailyExpenses) {
        Long totalIncome = StatCalculator.calculateSum(AccountBookCostExtractor.extract(dailyIncomes)).longValue();
        Long totalExpense = StatCalculator.calculateSum(AccountBookCostExtractor.extract(dailyExpenses)).longValue();

        List<AccountBookInfoResponse> dailyDetails = getDailyAccountBookDetails(dailyIncomes, dailyExpenses).stream()
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

    private List<AccountBook> getDailyAccountBookDetails(List<AccountBook> dailyIncomes, List<AccountBook> dailyExpenses) {
        return  Stream.concat(dailyIncomes.stream(), dailyExpenses.stream())
                .sorted(Comparator.comparing(AccountBook::getCreatedDate))
                .toList();
    }

    private AccountBookType inferAccountBookType(AccountBook accountBook) {
        if (accountBook.getPaymentMethod() != null) {
            return AccountBookType.EXPENSE;
        }

        return  AccountBookType.INCOME;
    }
}
