package com.poortorich.like.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.like.constants.LikeResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LikeResponse implements Response {

    GET_LIKE_STATUS_SUCCESS(HttpStatus.OK, LikeResponseMessage.GET_LIKE_STATUS_SUCCESS, null),
    UPDATE_LIKE_STATUS_SUCCESS(HttpStatus.OK, LikeResponseMessage.UPDATE_LIKE_STATUS_SUCCESS, null);

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
