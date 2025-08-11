package com.poortorich.chat.realtime.payload.request;

import com.poortorich.chat.constants.ChatResponseMessage;
import com.poortorich.chat.entity.enums.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageRequestPayload {

    @NotNull(message = ChatResponseMessage.CHATROOM_ID_REQUIRED)
    private Long chatroomId;

    @NotNull(message = ChatResponseMessage.MESSAGE_TYPE_REQUIRED)
    private MessageType messageType;

    private String content;
}
