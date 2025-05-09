package com.poortorich.expense.response;

import com.poortorich.expense.constants.ExpenseResponseMessages;
import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ExpenseResponse implements Response {

    CREATE_EXPENSE_SUCCESS(HttpStatus.CREATED, ExpenseResponseMessages.CREATE_EXPENSE_SUCCESS, null),

    TITLE_TOO_SHORT(HttpStatus.BAD_REQUEST, ExpenseResponseMessages.TITLE_TOO_SHORT, "title"),
    PAYMENT_METHOD_INVALID(HttpStatus.BAD_REQUEST, ExpenseResponseMessages.PAYMENT_METHOD_INVALID, "paymentMethod"),
    ITERATION_TYPE_INVALID(HttpStatus.BAD_REQUEST, ExpenseResponseMessages.ITERATION_TYPE_INVALID, "iterationType"),
    DATE_INVALID(HttpStatus.BAD_REQUEST, ExpenseResponseMessages.DATE_INVALID, "date");

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
