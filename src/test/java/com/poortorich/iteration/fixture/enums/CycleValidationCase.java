package com.poortorich.iteration.fixture.enums;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.iteration.constants.IterationResponseMessages;

public enum CycleValidationCase implements TestCase<Integer, String> {

    NULL(null, IterationResponseMessages.CYCLE_REQUIRED),
    TOO_SMALL(0, IterationResponseMessages.CYCLE_TOO_SMALL),
    TOO_BIG(366, IterationResponseMessages.CYCLE_TOO_BIG);

    private final Integer cycle;
    private final String expectedErrorMessage;

    CycleValidationCase(Integer cycle, String expectedErrorMessage) {
        this.cycle = cycle;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public Integer getTestData() {
        return cycle;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
