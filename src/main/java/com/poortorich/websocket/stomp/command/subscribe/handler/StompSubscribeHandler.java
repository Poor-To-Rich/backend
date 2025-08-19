package com.poortorich.websocket.stomp.command.subscribe.handler;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.repository.ChatroomRepository;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.chat.validator.ChatroomValidator;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.response.enums.UserResponse;
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
        Long chatroomId = endpointExtractor.getChatroomId(accessor.getDestination());
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new NotFoundException(ChatResponse.CHATROOM_NOT_FOUND));
        log.info("[SUBSCRIBE]: 유효한 채팅방 엔드포인트: {}", chatroomId);

        String username = sessionManager.getUsername(accessor);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND));
        log.info("[SUBSCRIBE]: 유저 조회 성공: {}", username);

        chatroomValidator.validateSubscribe(user, chatroom);
        log.info("[SUBSCRIBE]: 유저 `{}`가 채팅방[{}] 구독 완료", username, chatroom.getId());

        subscribeService.subscribe(chatroomId, username, accessor.getSessionId(), accessor.getSubscriptionId());
    }
}
