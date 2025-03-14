package com.poortorich.global.exceptions;

import com.poortorich.global.response.Response;
import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {

    private final Response response;

    public AuthenticationException(Response response) {
        super(response.getMessage());
        this.response = response;
    }

}
