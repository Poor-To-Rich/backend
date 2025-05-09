package com.poortorich.global.exceptions;

import com.poortorich.global.response.Response;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final Response response;
    private final String field;

    public NotFoundException(Response response, String field) {
        super(response.getMessage());
        this.response = response;
        this.field = field;
    }

    public NotFoundException(Response response) {
        super(response.getMessage());
        this.response = response;
        this.field = null;
    }
}
