package com.poortorich.chat.realtime.event.chatroom;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.response.MyChatroom;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.util.mapper.ChatroomMapper;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
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
    private final ChatroomMapper chatroomMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChatroomUpdated(ChatroomUpdateEvent event) {
        List<ChatParticipant> participants = chatParticipantService.findAllByChatroom(event.getChatroom());

        participants.forEach(participant -> {
            MyChatroom myChatroom = chatroomMapper.mapToMyChatroom(participant);

            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + participant.getUser().getId(),
                    myChatroom);
        });
    }
}
