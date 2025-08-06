package com.poortorich.websocket.stomp.command.publish.validator;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.user.service.UserService;
import com.poortorich.websocket.stomp.command.publish.endpoint.PublishEndpoint;
import com.poortorich.websocket.stomp.response.StompResponse;
import com.poortorich.websocket.stomp.util.StompSessionManager;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompPublishValidator {

    private final StompSessionManager sessionManager;
    private final UserService userService;

    public void validateDestination(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        boolean isMatching = PublishEndpoint.PUBLISH_PREFIXES.stream()
                .anyMatch(prefix -> destination.startsWith(prefix));

        if (!isMatching) {
            throw new BadRequestException(StompResponse.DESTINATION_INVALID);
        }
    }

    public void validateSession(StompHeaderAccessor accessor) {
        try {
            String username = sessionManager.getUsername(accessor);
            if (!Objects.isNull(username)) {
                userService.findUserByUsername(username);
            }
        } catch (Exception exception) {
            throw new BadRequestException(StompResponse.DESTINATION_INVALID);
        }
    }
}
