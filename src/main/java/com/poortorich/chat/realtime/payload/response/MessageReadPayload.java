package com.poortorich.chat.realtime.payload.response;

import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.realtime.payload.interfaces.ResponsePayload;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageReadPayload implements ResponsePayload {

    private Long chatroomId;
    private Long lastReadMessageId;
    private Long userId;
    private LocalDateTime readAt;

    @Override
    public BasePayload mapToBasePayload() {
        return BasePayload.builder()
                .type(ChatMessageType.MESSAGE_READ)
                .payload(this)
                .build();
    }
}
