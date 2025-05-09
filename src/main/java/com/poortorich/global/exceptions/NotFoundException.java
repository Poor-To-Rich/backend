package com.poortorich.global.exceptions;

import com.poortorich.global.response.Response;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final Response response;

    public NotFoundException(Response response) {
        super(response.getMessage());
        this.response = response;
    }
}
