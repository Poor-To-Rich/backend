package com.poortorich.iteration.fixture.enums;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.request.MonthlyOption;

public enum MonthlyOptionValidationCase implements TestCase<MonthlyOption, String> {

    DAY_MODE_DAY_NULL(
            new MonthlyOption("dayOfMonth", null, null, null),
            IterationResponseMessages.MONTHLY_OPTION_DAY_REQUIRED
    ),
    WEEKDAY_MODE_WEEK_NULL(
            new MonthlyOption("weekdayOfMonth", null, null, "월"),
            IterationResponseMessages.MONTHLY_OPTION_WEEK_REQUIRED
    );

    private final MonthlyOption monthlyOption;
    private final String expectedMessage;

    MonthlyOptionValidationCase(MonthlyOption monthlyOption, String expectedMessage) {
        this.monthlyOption = monthlyOption;
        this.expectedMessage = expectedMessage;
    }

    @Override
    public MonthlyOption getTestData() {
        return monthlyOption;
    }

    @Override
    public String getExpectedData() {
        return expectedMessage;
    }
}
