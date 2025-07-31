package com.poortorich.websocket.stomp.command.unsubscribe.handler;

import com.poortorich.websocket.stomp.command.subscribe.validator.SubscribeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompUnsubscribeHandler {

    private final SubscribeValidator subscribeValidator;

    public void handle(StompHeaderAccessor accessor) {
        subscribeValidator.validateEndPoint(accessor);
    }
}
