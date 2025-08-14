package com.poortorich.chat.realtime.event.datechange;

import com.poortorich.chat.realtime.payload.response.DateChangeMessagePayload;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DateChangeEventListner {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onDateChanged(DateChangeEvent event) {
        DateChangeMessagePayload payload = chatMessageService.saveDateChangeMessage(event.getChatroom());

        if (!Objects.isNull(payload)) {
            messagingTemplate.convertAndSend(SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + event.getChatroom().getId(),
                    payload.mapToBasePayload());
        }
    }
}
