package com.poortorich.chat.realtime.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.MessageType;
import com.poortorich.chat.model.ChatMessageResponse;
import com.poortorich.chat.realtime.payload.interfaces.ResponsePayload;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserChatMessagePayload extends ChatMessageResponse implements ResponsePayload {

    private Long messageId;
    private Long chatroomId;
    private Long senderId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long photoId;

    private MessageType messageType;
    private String content;
    private LocalDateTime sentAt;
    private List<Long> unreadBy;
    private ChatMessageType type;

    @Override
    public BasePayload mapToBasePayload() {
        return BasePayload.builder()
                .type(ChatMessageType.CHAT_MESSAGE)
                .payload(this)
                .build();
    }
}
