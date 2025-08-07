package com.poortorich.chatnotice.response.enums;

import com.poortorich.chatnotice.constants.ChatNoticeResponseMessage;
import com.poortorich.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ChatNoticeResponse implements Response {

    UPDATE_CHAT_NOTICE_STATUS_SUCCESS(HttpStatus.OK, ChatNoticeResponseMessage.UPDATE_CHAT_NOTICE_STATUS_SUCCESS, null),

    NOTICE_STATUS_INVALID(HttpStatus.BAD_REQUEST, ChatNoticeResponseMessage.NOTICE_STATUS_INVALID, "status");

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
