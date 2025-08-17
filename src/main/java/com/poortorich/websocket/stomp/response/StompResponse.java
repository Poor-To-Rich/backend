package com.poortorich.websocket.stomp.response;

import com.poortorich.global.response.Response;
import com.poortorich.websocket.stomp.constants.StompResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum StompResponse implements Response {

    SESSION_ATTRIBUTE_INVALID(HttpStatus.BAD_REQUEST, StompResponseMessage.SESSION_ATTRIBUTE_INVALID, "STOMP"),
    DESTINATION_NOT_FOUND(HttpStatus.NOT_FOUND, StompResponseMessage.DESTINATION_NOT_FOUND, "STOMP"),
    DESTINATION_INVALID(HttpStatus.BAD_REQUEST, StompResponseMessage.DESTINATION_INVALID, "STOMP"),
    UNSUBSCRIBE_FORBIDDEN(HttpStatus.FORBIDDEN, StompResponseMessage.UNSUBSCRIBE_FORBIDDEN, "STOMP"),
    SUBSCRIBER_SAVE_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, StompResponseMessage.SUBSCRIBER_SAVE_FAILURE, null),
    UNSUBSCRIBER_REMOVE_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, StompResponseMessage.UNSUBSCRIBER_REMOVE_FAILURE, null);

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
