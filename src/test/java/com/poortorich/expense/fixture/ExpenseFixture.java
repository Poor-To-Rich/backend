package com.poortorich.expense.fixture;

import java.time.LocalDate;

public class ExpenseFixture {

    public static final LocalDate VALID_DATE = LocalDate.of(2025, 1, 1);

    public static final String VALID_CATEGORY_NAME = "주거비";

    public static final String VALID_TITLE = "월세";
    public static final String SPACED_TITLE = "  월세 ";

    public static final Long VALID_COST = 700000L;

    public static final String VALID_PAYMENT_METHOD_STRING = "계좌이체";

    public static final String VALID_MEMO = "월세 너무 비싸다";

    public static final String VALID_ITERATION_TYPE_STRING = "매달";

    private ExpenseFixture() {}
}
