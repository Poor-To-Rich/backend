package com.poortorich.photo.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.photo.constants.PhotoResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PhotoResponse implements Response {

    UPLOAD_PHOTO_SUCCESS(HttpStatus.CREATED, PhotoResponseMessage.UPLOAD_PHOTO_SUCCESS, null),

    PHOTO_REQUIRED(HttpStatus.BAD_REQUEST, PhotoResponseMessage.PHOTO_REQUIRED , "photo");

    private final HttpStatus httpStatus;
    private final String message;
    private final String field;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getField() {
        return field;
    }
}
