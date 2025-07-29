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
    CHATROOM_ENTER_DENIED(HttpStatus.FORBIDDEN, ChatResponseMessage.CHATROOM_ENTER_DENIED, null),
    CHATROOM_PASSWORD_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, ChatResponseMessage.CHATROOM_PASSWORD_DO_NOT_MATCH, null),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, ChatResponseMessage.CHATROOM_NOT_FOUND, null),
    CHATROOM_ENTER_SUCCESS(HttpStatus.OK, ChatResponseMessage.CHATROOM_ENTER_SUCCESS, null),
    CHATROOM_ENTER_DUPLICATED(HttpStatus.CONFLICT, ChatResponseMessage.CHATROOM_ENTER_DUPLICATED, null);

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
