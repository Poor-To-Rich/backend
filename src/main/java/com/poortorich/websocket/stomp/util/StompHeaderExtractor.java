package com.poortorich.websocket.stomp.util;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHeaderExtractor {

    public String extractAccessToken(StompHeaderAccessor accessor) {
        return extractByHeaderName(accessor, "Authorization");
    }

    private String extractByHeaderName(StompHeaderAccessor accessor, String headerName) {
        return accessor.getFirstNativeHeader(headerName);
    }
}
