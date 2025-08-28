package com.poortorich.chat.realtime.event.chatroom;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chat.realtime.payload.response.enums.PayloadType;
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
public class ChatroomUpdateEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatParticipantService chatParticipantService;
    private final ChatroomMapper chatroomMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChatroomUpdated(ChatroomUpdateEvent event) {
        List<ChatParticipant> participants = chatParticipantService.findAllByChatroom(event.getChatroom());

        participants.forEach(participant -> {
            BasePayload basePayload = BasePayload.builder()
                    .type(PayloadType.JOINED_CHATROOMS_UPDATED)
                    .payload(chatroomMapper.mapToMyChatroom(participant))
                    .build();

            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.JOINED_CHATROOM_LIST_PREFIX + participant.getUser().getId(),
                    basePayload);
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChatParticipantUpdated(ParticipantUpdateEvent event) {
        ChatParticipant participant = event.getParticipant();

        BasePayload basePayload = BasePayload.builder()
                .type(PayloadType.JOINED_CHATROOMS_UPDATED)
                .payload(chatroomMapper.mapToMyChatroom(participant))
                .build();

        messagingTemplate.convertAndSend(
                SubscribeEndpoint.JOINED_CHATROOM_LIST_PREFIX + participant.getUser().getId(),
                basePayload);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserChatroomUpdated(UserChatroomUpdateEvent event) {
        List<ChatParticipant> participants = chatParticipantService.findAllByUser(event.getUser());

        List<MyChatroom> myChatrooms = participants.stream()
                .map(chatroomMapper::mapToMyChatroom)
                .toList();

        BasePayload basePayload = BasePayload.builder()
                .type(PayloadType.JOINED_CHATROOMS_UPDATED)
                .payload(myChatrooms)
                .build();

        messagingTemplate.convertAndSend(
                SubscribeEndpoint.JOINED_CHATROOM_LIST_PREFIX + event.getUser().getId(),
                basePayload
        );
    }
}
