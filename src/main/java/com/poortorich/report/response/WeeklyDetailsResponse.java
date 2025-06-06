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
public class WeeklyDetailsResponse {

    private String period;
    private Long totalIncome;
    private Long totalExpense;
    private Long totalAmount;
    private Long countOfLogs;
    private Boolean hasNext;
    private String nextCursor;
    private List<DailyTransaction> dailyDetails;
}
