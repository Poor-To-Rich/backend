package com.poortorich.websocket.stomp;

import com.poortorich.websocket.stomp.validator.StompValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompSessionManager {

    private static final String USERNAME_KEY = "username";

    private final StompValidator stompValidator;

    public void setUsername(StompHeaderAccessor accessor, String username) {
        setValue(accessor, USERNAME_KEY, username);
    }

    public String getUsername(StompHeaderAccessor accessor) {
        return (String) getValue(accessor, USERNAME_KEY);
    }

    private void setValue(StompHeaderAccessor accessor, String key, Object value) {
        stompValidator.validateSessionAttribute(accessor);
        accessor.getSessionAttributes().put(key, value);
    }

    private Object getValue(StompHeaderAccessor accessor, String key) {
        stompValidator.validateSessionAttribute(accessor);
        return accessor.getSessionAttributes().get(key);
    }
}
