package com.poortorich.chat.realtime.event.chatroom;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.service.ChatParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatroomUpdateEventListner {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatParticipantService chatParticipantService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChatroomUpdated(ChatroomUpdateEvent event) {
        List<ChatParticipant> participants = chatParticipantService.findAllByChatroom(event.getChatroom());

        participants.forEach(participant -> {
            
        });
    }
}
