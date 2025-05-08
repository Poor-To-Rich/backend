package com.poortorich.expense.response;

import com.poortorich.expense.constants.ExpenseResponseMessages;
import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ExpenseResponse implements Response {

    CREATE_EXPENSE_SUCCESS(HttpStatus.CREATED, ExpenseResponseMessages.CREATE_EXPENSE_SUCCESS),
    GET_EXPENSE_SUCCESS(HttpStatus.OK, ExpenseResponseMessages.GET_EXPENSE_SUCCESS),

    TITLE_TOO_SHORT(HttpStatus.BAD_REQUEST, ExpenseResponseMessages.TITLE_TOO_SHORT),
    PAYMENT_METHOD_INVALID(HttpStatus.BAD_REQUEST, ExpenseResponseMessages.PAYMENT_METHOD_INVALID),
    ITERATION_TYPE_INVALID(HttpStatus.BAD_REQUEST, ExpenseResponseMessages.ITERATION_TYPE_INVALID),
    DATE_INVALID(HttpStatus.BAD_REQUEST, ExpenseResponseMessages.DATE_INVALID),

    EXPENSE_NON_EXISTENT(HttpStatus.NOT_FOUND, ExpenseResponseMessages.EXPENSE_NON_EXISTENT);

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
