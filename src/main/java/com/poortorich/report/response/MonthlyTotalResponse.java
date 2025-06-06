package com.poortorich.report.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyTotalResponse {

    private Long totalAmount;
    private Long totalIncome;
    private Long totalExpense;
    private List<DailyFinance> transactions;
}
