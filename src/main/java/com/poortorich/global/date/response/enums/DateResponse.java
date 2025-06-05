package com.poortorich.global.date.response.enums;

import com.poortorich.global.date.constants.DateResponseMessage;
import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum DateResponse implements Response {

    UNSUPPORTED_DATE_FORMAT(HttpStatus.BAD_REQUEST, DateResponseMessage.UNSUPPORTED_DATE_FORMAT, "date");

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
