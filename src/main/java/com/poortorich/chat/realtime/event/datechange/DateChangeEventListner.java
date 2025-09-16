package com.poortorich.chat.realtime.event.datechange;

import com.poortorich.broadcast.BroadcastService;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.realtime.payload.response.DateChangeMessagePayload;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DateChangeEventListner {

    private final BroadcastService broadcastService;
    private final ChatMessageService chatMessageService;
    private final ChatroomService chatroomService;

    @EventListener
    public void onDateChanged(DateChangeEvent event) {
        Chatroom chatroom = chatroomService.findById(event.getChatroomId());
        DateChangeMessagePayload payload = chatMessageService.saveDateChangeMessage(chatroom);

        if (!Objects.isNull(payload)) {
            broadcastService.broadcastInChatroom(chatroom.getId(), payload.mapToBasePayload());
        }
    }
}
