package com.poortorich.global.exceptions;

import com.poortorich.global.response.Response;

public class ForbiddenException extends RuntimeException {

    private final Response response;

    public ForbiddenException(Response response) {
        super(response.getMessage());
        this.response = response;
    }

}
