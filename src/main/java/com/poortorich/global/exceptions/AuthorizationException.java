package com.poortorich.global.exceptions;

import com.poortorich.global.response.Response;
import lombok.Getter;

@Getter
public class AuthorizationException extends RuntimeException {

    private final Response response;

    public AuthorizationException(Response response) {
        super(response.getMessage());
        this.response = response;
    }
}
