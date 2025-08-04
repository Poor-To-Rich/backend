package com.poortorich.chat.realtime.payload.response;

import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.MessageType;
import com.poortorich.chat.realtime.payload.interfaces.ResponsePayload;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatroomClosedResponsePayload implements ResponsePayload {

    private Long messageId;
    private Long chatroomId;
    private MessageType messageType;
    private String content;
    private LocalDateTime sendAt;

    @Override
    public BasePayload mapToBasePayload() {
        return BasePayload.builder()
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .payload(this)
                .build();
    }
}
