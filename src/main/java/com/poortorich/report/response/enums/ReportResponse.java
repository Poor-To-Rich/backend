package com.poortorich.report.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.report.constants.ReportResponseMessages;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ReportResponse implements Response {

    GET_DAILY_DETAILS_SUCCESS(HttpStatus.OK, ReportResponseMessages.GET_DAILY_DETAILS_SUCCESS, null);

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
