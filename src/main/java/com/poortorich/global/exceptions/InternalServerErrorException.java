package com.poortorich.global.exceptions;

import com.poortorich.global.response.Response;
import lombok.Getter;

@Getter
public class InternalServerErrorException extends RuntimeException {

    private final Response response;

    public InternalServerErrorException(Response response) {
        super(response.getMessage());
        this.response = response;
    }

}
