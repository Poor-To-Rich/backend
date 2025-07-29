package com.poortorich.chat.response.enums;

import com.poortorich.chat.constants.ChatResponseMessage;
import com.poortorich.global.response.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatResponse implements Response {

    CREATE_CHATROOM_SUCCESS(HttpStatus.CREATED, ChatResponseMessage.CREATE_CHATROOM_SUCCESS, null),
    GET_CHATROOM_SUCCESS(HttpStatus.OK, ChatResponseMessage.GET_CHATROOM_SUCCESS, null),

    CHATROOM_NON_EXISTENT(HttpStatus.NOT_FOUND, ChatResponseMessage.CHATROOM_NON_EXISTENT, "chatroomId");

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
