package com.poortorich.websocket.stomp.command.subscribe.handler;

import com.poortorich.chat.repository.ChatroomRepository;
import com.poortorich.chat.validator.ChatroomValidator;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import com.poortorich.websocket.stomp.command.subscribe.util.SubscribeEndpointExtractor;
import com.poortorich.websocket.stomp.command.subscribe.validator.SubscribeValidator;
import com.poortorich.websocket.stomp.response.StompResponse;
import com.poortorich.websocket.stomp.service.SubscribeService;
import com.poortorich.websocket.stomp.util.StompSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompSubscribeHandler {

    private final UserRepository userRepository;
    private final ChatroomRepository chatroomRepository;
    private final ChatroomValidator chatroomValidator;

    private final SubscribeService subscribeService;
    private final StompSessionManager sessionManager;
    private final SubscribeValidator subscribeValidator;
    private final SubscribeEndpointExtractor endpointExtractor;

    public void handle(StompHeaderAccessor accessor) {
        log.info("[SUBSCRIBE]: 구독 시작---------------------------");
        subscribeValidator.validateEndPoint(accessor);

        String destination = accessor.getDestination();

        if (destination.startsWith(SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX)) {
            subscribeValidator.validateChatroomSubscribe(accessor);
        } else if (destination.startsWith(SubscribeEndpoint.JOINED_CHATROOM_LIST_PREFIX)) {
            subscribeValidator.validateJoinedChatroomSubscribe(accessor);
        } else {
            throw new BadRequestException(StompResponse.DESTINATION_INVALID);
        }
        log.info("[SUBSCRIBE]: 구독 종료---------------------------");
    }
}
