package com.poortorich.chat.realtime.payload.response.enums;

import com.poortorich.websocket.payload.interfaces.EventType;

public enum PayloadType implements EventType {

    NOTICE,
    USER_JOINED,
    USER_UPDATED,
    CHATROOM_MESSAGE_UPDATED,
    CHATROOM_INFO_UPDATED
}
