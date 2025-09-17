package com.poortorich.chat.realtime.event.chatparticipants;

import com.poortorich.broadcast.BroadcastService;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.realtime.payload.response.KickChatParticipantMessagePayload;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class KickChatroomEventListener {

    private final ChatParticipantService chatParticipantService;
    private final ChatMessageService chatMessageService;
    private final BroadcastService broadcastService;

    @EventListener
    public void onKickChatroomEvent(KickChatroomEvent event) {
        ChatParticipant participant = chatParticipantService.findByIdOrThrow(event.getChatParticipantId());
        KickChatParticipantMessagePayload payload = chatMessageService.saveKickChatParticipantMessage(participant);
        participant.updateKickMessageId(participant.getKickMessageId());

        if (Objects.nonNull(payload)) {
            broadcastService.broadcastInChatroom(participant.getChatroom().getId(), payload.mapToBasePayload());
        }
    }
}
