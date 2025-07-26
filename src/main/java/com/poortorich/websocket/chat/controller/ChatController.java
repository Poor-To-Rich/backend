package com.poortorich.websocket.chat.controller;

import com.poortorich.websocket.chat.request.ChatMessageRequest;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import com.poortorich.websocket.stomp.util.StompSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final StompSessionManager sessionManager;

    @MessageMapping("/chat/messages")
    public void handleChatMessage(@Payload ChatMessageRequest chatMessageRequest, StompHeaderAccessor accessor) {
        String username = sessionManager.getUsername(accessor);
        String destination = SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatMessageRequest.getChatroomId();
        messagingTemplate.convertAndSend(destination, chatMessageRequest);
    }
}
