package com.poortorich.websocket.stomp.validator;

import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.websocket.stomp.endpoint.SubscribeEndpoint;
import com.poortorich.websocket.stomp.response.StompResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscribeValidator {

    public final StompValidator stompValidator;

    public void validateEndPoint(StompHeaderAccessor accessor) {
        stompValidator.validateDestination(accessor);
        String subscribePath = accessor.getDestination();

        if (!hasMatchingPrefix(subscribePath)) {
            throw new NotFoundException(StompResponse.DESTINATION_NOT_FOUND);
        }
    }

    private boolean hasMatchingPrefix(String subPath) {
        return SubscribeEndpoint.SUB_PREFIXES.stream()
                .anyMatch(subPath::startsWith);
    }
}
