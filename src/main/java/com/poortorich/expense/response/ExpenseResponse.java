package com.poortorich.expense.response;

import com.poortorich.expense.constants.ExpenseResponseMessages;
import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ExpenseResponse implements Response {

    CREATE_EXPENSE_SUCCESS(HttpStatus.CREATED, ExpenseResponseMessages.CREATE_EXPENSE_SUCCESS, null),
    GET_EXPENSE_SUCCESS(HttpStatus.OK, ExpenseResponseMessages.GET_EXPENSE_SUCCESS, null),
    MODIFY_EXPENSE_SUCCESS(HttpStatus.CREATED, ExpenseResponseMessages.MODIFY_EXPENSE_SUCCESS, null),
    DELETE_EXPENSE_SUCCESS(HttpStatus.OK, ExpenseResponseMessages.DELETE_EXPENSE_SUCCESS, null),

    PAYMENT_METHOD_INVALID(HttpStatus.BAD_REQUEST, ExpenseResponseMessages.PAYMENT_METHOD_INVALID, "paymentMethod"),

    EXPENSE_NON_EXISTENT(HttpStatus.NOT_FOUND, ExpenseResponseMessages.EXPENSE_NON_EXISTENT, "expenseId");

    private final HttpStatus httpStatus;
    private final String message;
    private final String field;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getField() {
        return field;
    }
}
