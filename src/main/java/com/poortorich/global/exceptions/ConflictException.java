package com.poortorich.global.exceptions;

import com.poortorich.global.response.Response;
import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final Response response;
    private final String field;

    public ConflictException(Response response, String field) {
        super(response.getMessage());
        this.response = response;
        this.field = field;
    }
}
