package com.poortorich.tag.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.tag.constants.TagResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TagResponse implements Response {

    TAG_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, TagResponseMessage.TAG_NAME_TOO_LONG, "hashtags");

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
