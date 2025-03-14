package com.poortorich.global.exceptions;

import com.poortorich.global.response.Response;
import lombok.Getter;

@Getter
public class TooManyRequestException extends RuntimeException {

    Response response;

    public TooManyRequestException(Response response) {
        super(response.getMessage());
        this.response = response;
    }
}
