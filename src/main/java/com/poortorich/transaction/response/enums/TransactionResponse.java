package com.poortorich.transaction.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.transaction.constants.TransactionResponseMessages;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum TransactionResponse implements Response {

    GET_DAILY_DETAILS_SUCCESS(HttpStatus.OK, TransactionResponseMessages.GET_DAILY_DETAILS_SUCCESS, null),
    GET_WEEKLY_DETAILS_SUCCESS(HttpStatus.OK, TransactionResponseMessages.GET_WEEKLY_DETAILS_SUCCESS, null),
    GET_MONTHLY_TOTAL_SUCCESS(HttpStatus.OK, TransactionResponseMessages.GET_MONTHLY_TOTAL_SUCCESS, null),
    GET_YEARLY_TOTAL_SUCCESS(HttpStatus.OK, TransactionResponseMessages.GET_YEARLY_TOTAL_SUCCESS, null),
    GET_WEEKLY_TOTAL_SUCCESS(HttpStatus.OK, TransactionResponseMessages.GET_WEEKLY_TOTAL_SUCCESS, null),

    WEEK_INVALID(HttpStatus.BAD_REQUEST, TransactionResponseMessages.WEEK_INVALID, "week"),
    CURSOR_INVALID(HttpStatus.BAD_REQUEST, TransactionResponseMessages.CURSOR_INVALID, "cursor");

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
