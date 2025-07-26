package com.poortorich.websocket.stomp.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompSendHandler {

    public void handle(StompHeaderAccessor accessor) {
        
    }
}
