package com.poortorich.chat.entity.enums;

import com.poortorich.websocket.payload.interfaces.EventType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatMessageType implements EventType {

    CHAT_MESSAGE,
    SYSTEM_MESSAGE,
    RANKING_STATUS,
    MESSAGE_READ,
    RANKING_MESSAGE
}
