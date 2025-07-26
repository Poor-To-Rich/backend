package com.poortorich.websocket.stomp.handler;

import com.poortorich.websocket.stomp.StompSessionManager;
import com.poortorich.websocket.stomp.util.SubscribeEndpointExtractor;
import com.poortorich.websocket.stomp.validator.SubscribeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompSubscribeHandler {

    private final StompSessionManager sessionManager;
    private final SubscribeValidator subscribeValidator;
    private final SubscribeEndpointExtractor endpointExtractor;

    public void handle(StompHeaderAccessor accessor) {
        log.info("[SUBSCRIBE]: 구독 시작---------------------------");
        log.info("[SUBSCRIBE]: 엔드포인트 검증 시작");
        subscribeValidator.validateEndPoint(accessor);
        Long chatRoomId = endpointExtractor.getChatroomId(accessor.getDestination());
        log.info("[SUBSCRIBE]: 엔드포인트 검증 성공, 채팅방 아이디: {}", chatRoomId);
        log.info("[SUBSCRIBE]: 유저 정보 추출 시작");
        String username = sessionManager.getUsername(accessor);
        log.info("[SUBSCRIBE]: 유저 정보 추출 성공, username: {}", username);
        log.info("[SUBSCRIBE]: 채팅방[id={}]에 참여자[username:{}]가 참여할 수 있는지 검증", chatRoomId, username);
        log.info("[SUBSCRIBE]: 채팅방 검증 성공");
        log.info("[SUBSCRIBE]: 구독 성공---------------------------");
    }
}
