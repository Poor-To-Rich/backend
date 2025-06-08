package com.poortorich.income.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.income.constants.IncomeResponseMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum IncomeResponse implements Response {

    CREATE_INCOME_SUCCESS(HttpStatus.CREATED, IncomeResponseMessages.CREATE_INCOME_SUCCESS, null),
    GET_INCOME_SUCCESS(HttpStatus.OK, IncomeResponseMessages.GET_INCOME_SUCCESS, null),
    MODIFY_INCOME_SUCCESS(HttpStatus.CREATED, IncomeResponseMessages.MODIFY_INCOME_SUCCESS, null),
    DELETE_INCOME_SUCCESS(HttpStatus.OK, IncomeResponseMessages.DELETE_INCOME_SUCCESS, null),
    GET_INCOME_ITERATION_DETAILS_SUCCESS(HttpStatus.OK, IncomeResponseMessages.GET_INCOME_ITERATION_DETAILS_SUCCESS,
            null),

    INCOME_NON_EXISTENT(HttpStatus.NOT_FOUND, IncomeResponseMessages.INCOME_NON_EXISTENT, "incomeId");

    private final HttpStatus status;
    private final String message;
    private final String field;

    @Override
    public HttpStatus getHttpStatus() {
        return status;
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
