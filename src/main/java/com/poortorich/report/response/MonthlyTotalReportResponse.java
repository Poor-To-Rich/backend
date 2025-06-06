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
public class MonthlyTotalReportResponse {

    private Long yearTotalIncome;
    private Long yearTotalExpense;
    private Long yearTotalAmount;
    private List<Logs> logs;
}
