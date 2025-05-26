package com.poortorich.expense.fixture.enums;

import com.poortorich.accountbook.constants.AccountBookResponseMessages;
import com.poortorich.global.testcases.TestCase;

public enum MemoValidationCase implements TestCase<String, String> {
    TOO_LONG("아주아주아주아주아주아주아주아주아주아주아주아주아주아주아주아주아주아주아주아주엄청엄청엄청엄청엄청엄청엄청엄청엄청엄청엄청엄청엄청엄청엄청엄청긴메모를작성해야하는데아직도100자가넘지않았지만이제막넘",
            AccountBookResponseMessages.MEMO_TOO_LONG);

    private final String memo;
    private final String expectedErrorMessage;

    MemoValidationCase(String memo, String expectedErrorMessage) {
        this.memo = memo;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Override
    public String getTestData() {
        return memo;
    }

    @Override
    public String getExpectedData() {
        return expectedErrorMessage;
    }
}
