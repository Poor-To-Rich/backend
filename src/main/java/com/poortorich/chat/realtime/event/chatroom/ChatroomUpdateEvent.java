package com.poortorich.chat.realtime.event.chatroom;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.realtime.payload.response.enums.PayloadType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatroomUpdateEvent {

    private final Chatroom chatroom;
    private final PayloadType payloadType;
}
