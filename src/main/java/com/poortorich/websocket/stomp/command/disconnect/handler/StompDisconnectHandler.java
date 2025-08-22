package com.poortorich.websocket.stomp.command.disconnect.handler;

import com.poortorich.websocket.stomp.service.SubscribeService;
import com.poortorich.websocket.stomp.util.StompSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompDisconnectHandler {

    private final StompSessionManager sessionManager;
    private final SubscribeService subscribeService;
    private final StringRedisTemplate redisTemplate;

    public void handle(StompHeaderAccessor accessor) {
        String username = sessionManager.getUsername(accessor);
        String sessionId = accessor.getSessionId();

        subscribeService.cleanupSession(username, sessionId);
    }
}
