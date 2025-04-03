package com.poortorich.expense.fixture.enums;

import com.poortorich.expense.constants.ExpenseResponseMessages;
import com.poortorich.global.testcases.TestCase;

public enum CostValidationCase implements TestCase<Long, String> {
    NULL(null,ExpenseResponseMessages.COST_REQUIRED),
    ZERO(0L, ExpenseResponseMessages.COST_NEGATIVE),
    MINUS(-1L, ExpenseResponseMessages.COST_NEGATIVE),
    TOO_BIG(100000001L, ExpenseResponseMessages.COST_TOO_BIG);

    private final Long cost;
    private final String expectedErrorMessage;

    CostValidationCase(Long cost, String expectedErrorMessage) {
        this.cost = cost;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public Long getTestData() {
        return cost;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
