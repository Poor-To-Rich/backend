package com.poortorich.chart.util;

import com.poortorich.chart.response.TransactionRecord;
import com.poortorich.expense.entity.Expense;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransactionUtil {

    public static List<Expense> mergeExpensesByDate(List<Expense> expenseSlice, List<Expense> expenses) {
        return Stream.concat(
                        expenseSlice.stream(),
                        expenses.stream()
                )
                .distinct()
                .sorted(Comparator.comparing(Expense::getExpenseDate))
                .toList();
    }

    public static List<List<Expense>> groupExpensesByDate(List<Expense> expenses) {
        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getExpenseDate))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();
    }

    public static List<TransactionRecord> mapToTransactionRecord(List<Expense> expenses) {
        return expenses.stream()
                .map(expense -> TransactionRecord.builder()
                        .id(expense.getId())
                        .title(expense.getTitle())
                        .amount(expense.getCost())
                        .build())
                .toList();
    }
}
