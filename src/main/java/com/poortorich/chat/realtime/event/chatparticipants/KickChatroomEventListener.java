package com.poortorich.chat.realtime.event.chatparticipants;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.realtime.payload.response.KickChatParticipantMessagePayload;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class KickChatroomEventListener {

    private final ChatParticipantService chatParticipantService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onKickChatroomEvent(KickChatroomEvent event) {
        ChatParticipant participant = chatParticipantService.findByIdOrThrow(event.getChatParticipantId());
        KickChatParticipantMessagePayload payload = chatMessageService.saveKickChatParticipantMessage(participant);

        if (Objects.nonNull(payload)) {
            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + participant.getChatroom().getId(),
                    payload.mapToBasePayload());
        }
    }
}
