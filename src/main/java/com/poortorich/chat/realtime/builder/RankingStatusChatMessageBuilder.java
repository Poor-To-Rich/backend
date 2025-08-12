package com.poortorich.chat.realtime.builder;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.MessageType;

public class RankingStatusChatMessageBuilder {

    public static ChatMessage buildRankingStatusMessage(Chatroom chatroom) {
        return ChatMessage.builder()
                .chatroom(chatroom)
                .isRankingEnabled(chatroom.getIsRankingEnabled())
                .type(ChatMessageType.RANKING_STATUS)
                .messageType(MessageType.RANKING_STATUS)
                .build();
    }
}
