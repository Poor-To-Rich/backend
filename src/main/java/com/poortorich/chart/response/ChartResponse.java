package com.poortorich.chart.response;

import com.poortorich.chart.constants.ChartResponseMessage;
import com.poortorich.global.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ChartResponse implements Response {
    GET_TOTAL_EXPENSE_AND_SAVINGS_SUCCESS(
            HttpStatus.OK,
            ChartResponseMessage.GET_TOTAL_EXPENSE_AND_SAVINGS_SUCCESS,
            null
    );

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
