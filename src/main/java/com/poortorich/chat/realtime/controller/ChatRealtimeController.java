package com.poortorich.chat.realtime.controller;

import com.poortorich.chat.realtime.facade.ChatRealTimeFacade;
import com.poortorich.chat.realtime.payload.request.ChatMessageRequestPayload;
import com.poortorich.chat.realtime.payload.response.BasePayload;
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
public class ChatRealtimeController {

    private final ChatRealTimeFacade chatRealTimeFacade;

    private final SimpMessagingTemplate messagingTemplate;
    private final StompSessionManager sessionManager;

    @MessageMapping("/chat/messages")
    public void sendChatMessage(
            StompHeaderAccessor accessor,
            @Payload ChatMessageRequestPayload chatMessagePayload
    ) {
        String username = sessionManager.getUsername(accessor);
        BasePayload payload = chatRealTimeFacade.createUserChatMessage(username, chatMessagePayload);

        messagingTemplate.convertAndSend(
                SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatMessagePayload.getChatroomId(),
                payload);
    }
}
