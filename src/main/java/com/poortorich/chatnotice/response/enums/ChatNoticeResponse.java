package com.poortorich.chatnotice.response.enums;

import com.poortorich.chatnotice.constants.ChatNoticeResponseMessage;
import com.poortorich.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ChatNoticeResponse implements Response {

    GET_ALL_NOTICES_SUCCESS(HttpStatus.OK, ChatNoticeResponseMessage.GET_ALL_NOTICES_SUCCESS, null),
    GET_LATEST_NOTICE_SUCCESS(HttpStatus.OK, ChatNoticeResponseMessage.GET_LATEST_NOTICE_SUCCESS, null),
    GET_NOTICE_DETAILS_SUCCESS(HttpStatus.OK, ChatNoticeResponseMessage.GET_NOTICE_DETAILS_SUCCESS, null),
    UPDATE_NOTICE_STATUS_SUCCESS(HttpStatus.OK, ChatNoticeResponseMessage.UPDATE_NOTICE_STATUS_SUCCESS, null),
    GET_PREVIEW_NOTICE_SUCCESS(HttpStatus.OK, ChatNoticeResponseMessage.GET_PREVIEW_NOTICE_SUCCESS, null),

    NOTICE_STATUS_INVALID(HttpStatus.BAD_REQUEST, ChatNoticeResponseMessage.NOTICE_STATUS_INVALID, "status"),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, ChatNoticeResponseMessage.NOTICE_NOT_FOUND, null),
    CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, ChatNoticeResponseMessage.NOTICE_CONTENT_REQUIRED, null);

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
