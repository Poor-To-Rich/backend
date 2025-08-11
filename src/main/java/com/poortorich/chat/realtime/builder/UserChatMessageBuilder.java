package com.poortorich.chat.realtime.builder;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.realtime.payload.request.ChatMessageRequestPayload;

public class UserChatMessageBuilder {

    private UserChatMessageBuilder() {
    }

    public static ChatMessage buildChatMessage(
            ChatParticipant chatParticipant,
            ChatMessageRequestPayload chatMessageRequestPayload
    ) {
        return ChatMessage.builder()
                .userId(chatParticipant.getUser().getId())
                .type(ChatMessageType.CHAT_MESSAGE)
                .messageType(chatMessageRequestPayload.getMessageType())
                .content(chatMessageRequestPayload.getContent())
                .chatroom(chatParticipant.getChatroom())
                .build();
    }
}
