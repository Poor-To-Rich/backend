package com.poortorich.report.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyFinance {

    private String date;
    private Long incomeAmount;
    private Long expenseAmount;
}
