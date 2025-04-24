package com.poortorich.iteration.fixture.enums;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.request.IterationRule;

public enum IterationRuleValidationCase implements TestCase<IterationRule, String> {

    NULL(null, IterationResponseMessages.ITERATION_RULE_REQUIRED),
    ITERATION_RULE_TYPE_NULL(
         new IterationRule(null, null, null),
         IterationResponseMessages.ITERATION_RULE_TYPE_REQUIRED
    ),
    WEEKLY_TYPE_DAYS_OF_WEEK_NULL(
            new IterationRule("weekly", null, null),
            IterationResponseMessages.DAYS_OF_WEEK_REQUIRED_WEEKLY_TYPE
    ),
    MONTHLY_TYPE_MONTHLY_OPTION_NULL(
            new IterationRule("monthly", null, null),
            IterationResponseMessages.MONTHLY_OPTION_REQUIRED_MONTHLY_TYPE
    );

    private final IterationRule iterationRule;
    private final String expectedErrorMessage;

    IterationRuleValidationCase(IterationRule iterationRule, String expectedErrorMessage) {
        this.iterationRule = iterationRule;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public IterationRule getTestData() {
        return iterationRule;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
