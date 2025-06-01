package com.poortorich.expense.fixture.enums;

import com.poortorich.accountbook.constants.AccountBookResponseMessages;
import com.poortorich.global.testcases.TestCase;

import java.time.LocalDate;

public enum DateValidationCase implements TestCase<LocalDate, String> {
    NULL(null, AccountBookResponseMessages.DATE_REQUIRED);

    private final LocalDate date;
    private final String expectedErrorMessage;

    DateValidationCase(LocalDate date, String expectedErrorMessage) {
        this.date = date;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public LocalDate getTestData() {
        return date;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
