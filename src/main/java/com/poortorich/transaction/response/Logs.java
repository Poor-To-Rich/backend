package com.poortorich.transaction.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Logs {

    private String period;
    private Long totalIncome;
    private Long totalExpense;
    private Long totalAmount;
}
