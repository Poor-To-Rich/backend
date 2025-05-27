package com.poortorich.global.date.response.enums;

import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum DateResponse implements Response {

    UNSUPPORTED_DATE_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 날짜 형식입니다.", "date");

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
