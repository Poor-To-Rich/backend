package com.poortorich.admin.response.enums;

import com.poortorich.admin.constants.AdminResponseMessage;
import com.poortorich.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AdminResponse implements Response {

    ROLE_UPDATE_SUCCESS(HttpStatus.OK, AdminResponseMessage.ROLE_UPDATE_SUCCESS, null);

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
