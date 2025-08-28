package com.poortorich.websocket.stomp.command.subscribe.validator;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.repository.ChatroomRepository;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.chat.validator.ChatroomValidator;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.response.enums.UserResponse;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import com.poortorich.websocket.stomp.command.subscribe.util.SubscribeEndpointExtractor;
import com.poortorich.websocket.stomp.response.StompResponse;
import com.poortorich.websocket.stomp.service.SubscribeService;
import com.poortorich.websocket.stomp.util.StompSessionManager;
import com.poortorich.websocket.stomp.validator.StompValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscribeValidator {

    private final UserRepository userRepository;
    private final ChatroomRepository chatroomRepository;
    private final ChatroomValidator chatroomValidator;

    private final SubscribeService subscribeService;
    private final StompSessionManager sessionManager;
    private final StompValidator stompValidator;
    private final SubscribeEndpointExtractor endpointExtractor;

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

    public void validateChatroomSubscribe(StompHeaderAccessor accessor) {
        Long chatroomId = endpointExtractor.getDestinationValue(accessor.getDestination());
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

    public void validateJoinedChatroomSubscribe(StompHeaderAccessor accessor) {
        Long userId = endpointExtractor.getDestinationValue(accessor.getDestination());
        log.info("[SUBSCRIBE]: 구독하려는 회원 아이디 추출 성공 {}", userId);

        User subscriber = userRepository.findByUsername(sessionManager.getUsername(accessor))
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND));
        log.info("[SUBSCRIBE]: 구독 유저 조회 성공: {}", subscriber.getUsername());

        log.info("[SUBSCRIBE]: 구독자의 아이디[{}]가 요청 경로 아이디[{}]와 일치하는지 검사", subscriber.getId(), userId);
        if (!subscriber.getId().equals(userId)) {
            throw new BadRequestException(StompResponse.DESTINATION_INVALID);
        }
        log.info("[SUBSCRIBE]: 유저[{}]가 참여중인 채팅 목록 구독 완료", subscriber.getUsername());
    }
}
