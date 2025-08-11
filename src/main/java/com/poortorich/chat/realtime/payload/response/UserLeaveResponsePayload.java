package com.poortorich.chat.realtime.payload.response;

import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.MessageType;
import com.poortorich.chat.model.ChatMessageResponse;
import com.poortorich.chat.realtime.payload.interfaces.ResponsePayload;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class UserLeaveResponsePayload extends ChatMessageResponse implements ResponsePayload {

    private Long userId;
    private Long messageId;
    private Long chatroomId;
    private MessageType messageType;
    private String content;
    private LocalDateTime sendAt;
    private ChatMessageType type;

    @Override
    public BasePayload mapToBasePayload() {
        return BasePayload.builder()
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .payload(this)
                .build();
    }
}
