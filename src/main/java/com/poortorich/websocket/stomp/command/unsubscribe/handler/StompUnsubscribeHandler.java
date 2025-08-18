package com.poortorich.websocket.stomp.command.unsubscribe.handler;

import com.poortorich.websocket.stomp.command.subscribe.util.SubscribeEndpointExtractor;
import com.poortorich.websocket.stomp.command.subscribe.validator.SubscribeValidator;
import com.poortorich.websocket.stomp.service.SubscribeService;
import com.poortorich.websocket.stomp.util.StompSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompUnsubscribeHandler {

    private final SubscribeValidator subscribeValidator;
    private final SubscribeEndpointExtractor endpointExtractor;
    private final StompSessionManager sessionManager;
    private final SubscribeService subscribeService;

    public void handle(StompHeaderAccessor accessor) {
        subscribeValidator.validateEndPoint(accessor);
        Long chatroomId = endpointExtractor.getChatroomId(accessor.getDestination());
        String username = sessionManager.getUsername(accessor);

        subscribeService.unsubscribe(chatroomId, username);
    }
}
