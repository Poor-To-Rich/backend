package com.poortorich.expense.fixture.enums;

import com.poortorich.expense.constants.ExpenseResponseMessages;
import com.poortorich.global.testcases.TestCase;

public enum CategoryNameValidationCase implements TestCase<String, String> {
    NULL(null, ExpenseResponseMessages.CATEGORY_NAME_REQUIRED);

    private final String categoryName;
    private final String expectedErrorMessage;

    CategoryNameValidationCase(String categoryName, String expectedErrorMessage) {
        this.categoryName = categoryName;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public String getTestData() {
        return categoryName;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
