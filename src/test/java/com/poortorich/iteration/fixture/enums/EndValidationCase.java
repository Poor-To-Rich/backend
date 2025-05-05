package com.poortorich.iteration.fixture.enums;

import com.poortorich.global.testcases.TestCase;
import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.request.End;

public enum EndValidationCase implements TestCase<End, String> {

    NULL(null, IterationResponseMessages.END_REQUIRED),
    END_TYPE_NULL(
            new End(null, null, null),
            IterationResponseMessages.END_TYPE_REQUIRED
    ),
    AFTER_TYPE_COUNT_NULL(
            new End("after", null, null),
            IterationResponseMessages.END_COUNT_REQUIRED
    ),
    UNTIL_TYPE_DATE_NULL(
            new End("until", null, null),
            IterationResponseMessages.END_DATE_REQUIRED
    ),
    COUNT_TOO_SMALL(
            new End("after", 0, null),
            IterationResponseMessages.END_COUNT_TOO_SMALL
    ),
    COUNT_TOO_BIG(
            new End("after", 1000, null),
            IterationResponseMessages.END_COUNT_TOO_BIG
    );

    private final End end;
    private final String expectedErrorMessage;

    EndValidationCase(End end, String expectedErrorMessage) {
        this.end = end;
        this.expectedErrorMessage = expectedErrorMessage;
    }


    @Override
    public End getTestData() {
        return end;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
