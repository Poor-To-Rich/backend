package com.poortorich.chat.realtime.builder;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.MessageType;
import com.poortorich.ranking.entity.Ranking;

import java.util.Objects;

public class RankingMessageBuilder {

    private RankingMessageBuilder() {
    }

    public static ChatMessage buildRankingMessage(Chatroom chatroom, Ranking ranking) {
        if (Objects.isNull(ranking)) {
            return ChatMessage.builder()
                    .chatroom(chatroom)
                    .type(ChatMessageType.RANKING_MESSAGE)
                    .messageType(MessageType.RANKING)
                    .build();
        }
        
        return ChatMessage.builder()
                .rankingId(ranking.getId())
                .type(ChatMessageType.RANKING_MESSAGE)
                .messageType(MessageType.RANKING)
                .chatroom(ranking.getChatroom())
                .build();
    }
}
