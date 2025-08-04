package com.poortorich.chat.realtime.payload.request;

import com.poortorich.chat.entity.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageRequestPayload {

    private Long chatroomId;
    private MessageType messageType;
    private String content;
}
