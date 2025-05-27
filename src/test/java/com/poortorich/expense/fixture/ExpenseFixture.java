package com.poortorich.expense.fixture;

import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.Expense;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.expense.entity.enums.PaymentMethod;
import com.poortorich.iteration.request.CustomIteration;
import java.time.LocalDate;

public class ExpenseFixture {

    public static final String VALID_DATE = "2025-01-01";

    public static final String VALID_CATEGORY_NAME = "주거비";

    public static final String VALID_TITLE = "월세";
    public static final String SPACED_TITLE = "  월세 ";

    public static final Long VALID_COST = 700000L;

    public static final String VALID_PAYMENT_METHOD_STRING = "계좌이체";

    public static final String VALID_MEMO = "월세 너무 비싸다";

    public static final String VALID_ITERATION_TYPE_STRING = "monthly";

    public static final CustomIteration VALID_CUSTOM_ITERATION = null;

    private ExpenseFixture() {
    }

    public static Expense defaultExpense(LocalDate date) {
        return Expense.builder()
                .expenseDate(date)
                .title("회비")
                .cost(10000L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .iterationType(IterationType.CUSTOM)
                .category(Category.builder().name("회비").build())
                .build();
    }
}
