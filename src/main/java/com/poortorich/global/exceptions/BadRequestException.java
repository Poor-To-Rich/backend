package com.poortorich.global.exceptions;

import com.poortorich.global.response.Response;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final Response response;

    public BadRequestException(Response response) {
        super(response.getMessage());
        this.response = response;
    }
}
