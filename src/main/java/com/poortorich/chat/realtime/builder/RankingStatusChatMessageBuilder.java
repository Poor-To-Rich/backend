package com.poortorich.chat.realtime.builder;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;

public class RankingStatusChatMessageBuilder {

    public static ChatMessage buildRankingStatusMessage(Chatroom chatroom) {
        return ChatMessage.builder()
                .chatroom(chatroom)
                .isRankingEnabled(chatroom.getIsRankingEnabled())
                .type(ChatMessageType.RANKING_STATUS)
                .build();
    }
}
