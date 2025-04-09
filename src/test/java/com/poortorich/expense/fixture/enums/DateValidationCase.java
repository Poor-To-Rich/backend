package com.poortorich.expense.fixture.enums;

import com.poortorich.expense.constants.ExpenseResponseMessages;
import com.poortorich.global.testcases.TestCase;

import java.time.LocalDate;

public enum DateValidationCase implements TestCase<LocalDate, String> {
    NULL(null, ExpenseResponseMessages.DATE_REQUIRED);

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
