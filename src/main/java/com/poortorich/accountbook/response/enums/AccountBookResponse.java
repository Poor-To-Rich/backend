package com.poortorich.accountbook.response.enums;

import com.poortorich.accountbook.constants.AccountBookResponseMessages;
import com.poortorich.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AccountBookResponse implements Response {

    TITLE_TOO_SHORT(HttpStatus.BAD_REQUEST, AccountBookResponseMessages.TITLE_TOO_SHORT, "title"),
    ITERATION_TYPE_INVALID(HttpStatus.BAD_REQUEST, AccountBookResponseMessages.ITERATION_TYPE_INVALID, "iterationType"),
    DATE_INVALID(HttpStatus.BAD_REQUEST, AccountBookResponseMessages.DATE_INVALID, "date"),
    ITERATION_ACTION_INVALID(HttpStatus.BAD_REQUEST, AccountBookResponseMessages.ITERATION_ACTION_INVALID, "iterationAction");

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
