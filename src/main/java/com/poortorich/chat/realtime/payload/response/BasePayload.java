package com.poortorich.chat.realtime.payload.response;

import com.poortorich.websocket.payload.interfaces.EventType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BasePayload {

    private EventType type;
    private Object payload;
}
