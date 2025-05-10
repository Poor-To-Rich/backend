package com.poortorich.global.exceptions;

import com.poortorich.global.response.Response;
import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final Response response;

    public ConflictException(Response response) {
        super(response.getMessage());
        this.response = response;
    }
}
