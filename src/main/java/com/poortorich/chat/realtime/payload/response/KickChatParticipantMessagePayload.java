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
public class KickChatParticipantMessagePayload extends ChatMessageResponse implements ResponsePayload {

    private Long userId;
    private Long messageId;
    private Long chatroomId;
    private MessageType messageType;
    private ChatMessageType type;
    private String content;
    private LocalDateTime sentAt;

    @Override
    public BasePayload mapToBasePayload() {
        return BasePayload.builder()
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .payload(this)
                .build();
    }
}
