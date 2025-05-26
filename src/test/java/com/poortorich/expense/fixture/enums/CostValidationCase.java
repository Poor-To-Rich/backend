package com.poortorich.expense.fixture.enums;

import com.poortorich.accountbook.constants.AccountBookResponseMessages;
import com.poortorich.global.testcases.TestCase;

public enum CostValidationCase implements TestCase<Long, String> {
    NULL(null, AccountBookResponseMessages.COST_REQUIRED),
    ZERO(0L, AccountBookResponseMessages.COST_NEGATIVE),
    MINUS(-1L, AccountBookResponseMessages.COST_NEGATIVE),
    TOO_BIG(100000001L, AccountBookResponseMessages.COST_TOO_BIG);

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
