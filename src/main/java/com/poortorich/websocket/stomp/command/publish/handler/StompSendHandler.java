package com.poortorich.websocket.stomp.command.publish.handler;

import com.poortorich.websocket.stomp.command.publish.validator.StompPublishValidator;
import com.poortorich.websocket.stomp.validator.StompValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompSendHandler {

    private final StompValidator stompValidator;
    private final StompPublishValidator stompPublishValidator;

    public void handle(StompHeaderAccessor accessor) {
        stompValidator.validateDestination(accessor);
        stompPublishValidator.validate(accessor);
    }
}
