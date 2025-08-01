package com.poortorich.chat.realtime.payload;

import com.poortorich.chat.entity.enums.ChatMessageType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponsePayload {

    private ChatMessageType type;
    private Object payload;
}
