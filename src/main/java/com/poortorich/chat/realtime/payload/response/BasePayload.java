package com.poortorich.chat.realtime.payload.response;

import com.poortorich.chat.entity.enums.ChatMessageType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BasePayload {

    private ChatMessageType type;
    private Object payload;
}
