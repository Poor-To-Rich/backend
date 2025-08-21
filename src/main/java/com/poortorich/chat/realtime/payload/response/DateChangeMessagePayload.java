package com.poortorich.chat.realtime.payload.response;

import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.MessageType;
import com.poortorich.chat.model.ChatMessageResponse;
import com.poortorich.chat.realtime.payload.interfaces.ResponsePayload;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DateChangeMessagePayload extends ChatMessageResponse implements ResponsePayload {

    private final Long messageId;
    private final Long chatroomId;
    private final MessageType messageType;
    private final ChatMessageType type;
    private final String content;
    private final LocalDateTime sentAt;

    @Override
    public BasePayload mapToBasePayload() {
        return BasePayload.builder()
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .payload(this)
                .build();
    }
}
