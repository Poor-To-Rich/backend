package com.poortorich.expense.fixture.enums;

import com.poortorich.accountbook.constants.AccountBookResponseMessages;
import com.poortorich.global.testcases.TestCase;

public enum TitleValidationCase implements TestCase<String, String> {
    TOO_LONG("아주아주아주아주아주아주긴지출제목", AccountBookResponseMessages.TITLE_TOO_LONG);

    private final String title;
    private final String expectedErrorMessage;

    TitleValidationCase(String title, String expectedErrorMessage) {
        this.title = title;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public String getTestData() {
        return title;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
