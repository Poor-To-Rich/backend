package com.poortorich.chat.realtime.event.datechange;

import com.poortorich.chat.realtime.payload.response.DateChangeMessagePayload;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DateChangeEventListner {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @EventListener
    public void onDateChanged(DateChangeEvent event) {
        DateChangeMessagePayload payload = chatMessageService.saveDateChangeMessage(event.getChatroom());

        if (!Objects.isNull(payload)) {
            messagingTemplate.convertAndSend(SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + event.getChatroom().getId(),
                    payload.mapToBasePayload());
        }
    }
}
