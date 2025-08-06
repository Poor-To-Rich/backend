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
    GET_HOSTED_CHATROOMS_SUCCESS(HttpStatus.OK, ChatResponseMessage.GET_HOSTED_CHATROOMS_SUCCESS, null),
    GET_SEARCH_CHATROOMS_SUCCESS(HttpStatus.OK, ChatResponseMessage.GET_SEARCH_CHATROOMS_SUCCESS, null),
    GET_CHATROOM_DETAILS_SUCCESS(HttpStatus.OK, ChatResponseMessage.GET_CHATROOM_DETAILS_SUCCESS, null),
    GET_CHATROOM_COVER_INFO_SUCCESS(HttpStatus.OK, ChatResponseMessage.GET_CHATROOM_COVER_INFO_SUCCESS, null),

    CHATROOM_ENTER_DENIED(HttpStatus.FORBIDDEN, ChatResponseMessage.CHATROOM_ENTER_DENIED, null),
    CHATROOM_PASSWORD_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, ChatResponseMessage.CHATROOM_PASSWORD_DO_NOT_MATCH, null),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, ChatResponseMessage.CHATROOM_NOT_FOUND, "chatroomId"),
    CHATROOM_ENTER_SUCCESS(HttpStatus.OK, ChatResponseMessage.CHATROOM_ENTER_SUCCESS, null),
    CHATROOM_ENTER_DUPLICATED(HttpStatus.CONFLICT, ChatResponseMessage.CHATROOM_ENTER_DUPLICATED, null),
    CHATROOM_UPDATE_SUCCESS(HttpStatus.OK, ChatResponseMessage.CHATROOM_UPDATE_SUCCESS, null),
    CHAT_PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, ChatResponseMessage.CHAT_PARTICIPANT_NOT_FOUND, null),
    CHATROOM_NOT_PARTICIPATE(HttpStatus.BAD_REQUEST, ChatResponseMessage.CHATROOM_NOT_PARTICIPATE, null),
    CHAT_PARTICIPANT_NOT_HOST(HttpStatus.BAD_REQUEST, ChatResponseMessage.CHAT_PARTICIPANT_NOT_HOST, null),

    CHATROOM_MAX_MEMBER_COUNT_EXCEED(
            HttpStatus.BAD_REQUEST,
            ChatResponseMessage.CHATROOM_MAX_MEMBER_COUNT_EXCEED,
            null
    ),

    CHATROOM_LEAVE_ALREADY(HttpStatus.BAD_REQUEST, ChatResponseMessage.CHATROOM_LEAVE_ALREADY, null),
    CHATROOM_LEAVE_SUCCESS(HttpStatus.OK, ChatResponseMessage.CHATROOM_LEAVE_SUCCESS, null),
    CHAT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, ChatResponseMessage.CHAT_MESSAGE_NOT_FOUND, null),
    GET_CHAT_MESSAGE_SUCCESS(HttpStatus.OK, ChatResponseMessage.GET_CHAT_MESSAGE_SUCCESS, null);

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
