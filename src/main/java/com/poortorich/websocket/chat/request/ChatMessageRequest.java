package com.poortorich.websocket.chat.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageRequest {

    Long chatroomId;
    String messageType;
    String content;
}
