package com.poortorich.global.response;

import org.springframework.http.HttpStatus;

public interface Response {

    HttpStatus getHttpStatus();

    String getMessage();

    String getField();
}
