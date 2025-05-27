package com.poortorich.income.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.income.constants.IncomeResponseMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum IncomeResponse implements Response {

    CREATE_INCOME_SUCCESS(HttpStatus.CREATED, IncomeResponseMessages.CREATE_INCOME_SUCCESS, null);

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
