package com.poortorich.ranking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class UserExpenseAggregate {

    private final Long userId;
    private final Long totalCost;
    private final Long distinctExpenseDays;

    public int getExpenseDaysCount() {
        return distinctExpenseDays != null ? distinctExpenseDays.intValue() : 0;
    }

    public BigDecimal getTotalCostAsBigDecimal() {
        return totalCost != null ? BigDecimal.valueOf(totalCost) : BigDecimal.ZERO;
    }
}
