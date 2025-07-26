package com.poortorich.websocket.stomp.command.publish.validator;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.websocket.stomp.command.publish.endpoint.PublishEndpoint;
import com.poortorich.websocket.stomp.response.StompResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompPublishValidator {

    public void validate(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        boolean isMatching = PublishEndpoint.PUBLISH_PREFIXES.stream()
                .anyMatch(prefix -> destination.startsWith(prefix));

        if (!isMatching) {
            throw new BadRequestException(StompResponse.DESTINATION_INVALID);
        }
    }
}
