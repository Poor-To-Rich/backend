package com.poortorich.websocket.stomp.util;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.websocket.stomp.endpoint.SubscribeEndpoint;
import com.poortorich.websocket.stomp.response.StompResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscribeEndpointExtractor {

    public Long getChatroomId(String endpoint) {
        return SubscribeEndpoint.SUB_PREFIXES.stream()
                .filter(endpoint::startsWith)
                .findFirst()
                .map(prefix -> endpoint.substring(prefix.length()))
                .map(Long::valueOf)
                .orElseThrow(() -> new BadRequestException(StompResponse.DESTINATION_INVALID));
    }
}
