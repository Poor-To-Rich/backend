package com.poortorich.report.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.report.constants.ReportResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportResponse implements Response {

    MEMBER_REPORT_SUCCESS(HttpStatus.OK, ReportResponseMessage.MEMBER_REPORT_SUCCESS, null),

    REPORT_DUPLICATE(HttpStatus.CONFLICT, ReportResponseMessage.REPORT_DUPLICATE, "userId, chatroomId");

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
