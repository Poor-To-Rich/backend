package com.poortorich.expense.util;

import com.poortorich.expense.fixture.ExpenseFixture;
import com.poortorich.expense.request.ExpenseRequest;
import com.poortorich.iteration.request.CustomIteration;

import java.time.LocalDate;

public class ExpenseRequestTestBuilder {

    private String date = ExpenseFixture.VALID_DATE;
    private String categoryName = ExpenseFixture.VALID_CATEGORY_NAME;
    private String title = ExpenseFixture.VALID_TITLE;
    private Long cost = ExpenseFixture.VALID_COST;
    private String paymentMethod = ExpenseFixture.VALID_PAYMENT_METHOD_STRING;
    private String memo = ExpenseFixture.VALID_MEMO;
    private String iterationType = ExpenseFixture.VALID_ITERATION_TYPE_STRING;
    private CustomIteration customIteration = ExpenseFixture.VALID_CUSTOM_ITERATION;

    public ExpenseRequestTestBuilder date(String date) {
        this.date = date;
        return this;
    }

    public ExpenseRequestTestBuilder categoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public ExpenseRequestTestBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ExpenseRequestTestBuilder cost(Long cost) {
        this.cost = cost;
        return this;
    }

    public ExpenseRequestTestBuilder paymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public ExpenseRequestTestBuilder memo(String memo) {
        this.memo = memo;
        return this;
    }

    public ExpenseRequestTestBuilder iterationType(String iterationType) {
        this.iterationType = iterationType;
        return this;
    }

    public ExpenseRequestTestBuilder customIteration(CustomIteration customIteration) {
        this.customIteration = customIteration;
        return this;
    }

    public ExpenseRequest build() {
        return new ExpenseRequest(
                date,
                categoryName,
                title,
                cost,
                paymentMethod,
                memo,
                iterationType,
                customIteration
        );
    }
}
