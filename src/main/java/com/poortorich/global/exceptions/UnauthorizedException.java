package com.poortorich.global.exceptions;

import com.poortorich.global.response.Response;
import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

    private final Response response;

    public UnauthorizedException(Response response) {
        super(response.getMessage());
        this.response = response;
    }

}
