package com.poortorich.iteration.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.iteration.constants.IterationResponseMessages;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum IterationResponse implements Response {

    ITERATION_RULE_TYPE_INVALID(HttpStatus.BAD_REQUEST, IterationResponseMessages.ITERATION_RULE_TYPE_INVALID, "customIteration.iterationRule.type"),

    DAY_OF_WEEK_INVALID(HttpStatus.BAD_REQUEST, IterationResponseMessages.DAY_OF_WEEK_INVALID, "dayOfWeek"),

    MONTHLY_MODE_INVALID(HttpStatus.BAD_REQUEST, IterationResponseMessages.MONTHLY_MODE_INVALID, "customIteration.monthlyOption.mode"),

    END_TYPE_INVALID(HttpStatus.BAD_REQUEST, IterationResponseMessages.END_TYPE_INVALID, "customIteration.end.type"),
    END_DATE_INVALID(HttpStatus.BAD_REQUEST, IterationResponseMessages.END_DATE_INVALID, "customIteration.end.date"),
    END_DATE_NOT_BEFORE(HttpStatus.BAD_REQUEST, IterationResponseMessages.END_DATE_NOT_BEFORE, "customIteration.end.date"),

    ITERATIONS_TOO_LONG(HttpStatus.INTERNAL_SERVER_ERROR, IterationResponseMessages.ITERATIONS_TOO_LONG, null),

    ITERATION_EXPENSES_NOT_FOUND(HttpStatus.NOT_FOUND, IterationResponseMessages.ITERATION_EXPENSES_NOT_FOUND, null);

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
