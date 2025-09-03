package com.poortorich.chat.util.mapper;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.model.ChatMessageResponse;
import com.poortorich.chat.realtime.payload.response.ChatroomClosedResponsePayload;
import com.poortorich.chat.realtime.payload.response.DateChangeMessagePayload;
import com.poortorich.chat.realtime.payload.response.HostDelegationMessagePayload;
import com.poortorich.chat.realtime.payload.response.RankingStatusMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserChatMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserEnterResponsePayload;
import com.poortorich.chat.realtime.payload.response.UserLeaveResponsePayload;
import com.poortorich.chat.service.UnreadChatMessageService;
import com.poortorich.ranking.entity.Ranking;
import com.poortorich.ranking.payload.response.RankingResponsePayload;
import com.poortorich.ranking.service.RankingService;
import com.poortorich.ranking.util.mapper.RankerProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageMapper {

    private final UnreadChatMessageService unreadChatMessageService;
    private final RankingService rankingService;

    private final RankerProfileMapper rankerProfileMapper;

    public ChatMessageResponse mapToChatMessageResponse(ChatMessage chatMessage) {
        return switch (chatMessage.getMessageType()) {
            case RANKING -> rankingMessage(chatMessage);
            case RANKING_STATUS -> rankingStatusMessage(chatMessage);
            case ENTER -> userEnterMessage(chatMessage);
            case LEAVE, KICK -> userLeaveMessage(chatMessage);
            case CLOSE -> chatroomClosedMessage(chatMessage);
            case TEXT, PHOTO -> userChatMessage(chatMessage);
            case DATE -> dateChangeMessage(chatMessage);
            case DELEGATE -> hostDelegationMessage(chatMessage);
        };
    }

    private ChatMessageResponse rankingMessage(ChatMessage chatMessage) {
        Ranking ranking = rankingService.findById(chatMessage.getRankingId());

        return RankingResponsePayload.builder()
                .messageId(chatMessage.getId())
                .rankingId(chatMessage.getRankingId())
                .chatroomId(chatMessage.getChatroom().getId())
                .rankedAt(chatMessage.getSentAt().toLocalDate())
                .sentAt(chatMessage.getSentAt())
                .saverRankings(rankerProfileMapper.mapToSavers(ranking))
                .flexerRankings(rankerProfileMapper.mapToFlexer(ranking))
                .type(chatMessage.getType())
                .messageType(chatMessage.getMessageType())
                .build();
    }

    private ChatMessageResponse hostDelegationMessage(ChatMessage chatMessage) {
        return HostDelegationMessagePayload.builder()
                .messageId(chatMessage.getId())
                .messageType(chatMessage.getMessageType())
                .type(chatMessage.getType())
                .content(chatMessage.getContent())
                .chatroomId(chatMessage.getChatroom().getId())
                .sentAt(chatMessage.getSentAt())
                .build();
    }

    private ChatMessageResponse dateChangeMessage(ChatMessage chatMessage) {
        return DateChangeMessagePayload.builder()
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .messageType(chatMessage.getMessageType())
                .type(chatMessage.getType())
                .build();
    }

    private ChatMessageResponse rankingStatusMessage(ChatMessage chatMessage) {
        return RankingStatusMessagePayload.builder()
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .isRankingEnabled(chatMessage.getIsRankingEnabled())
                .messageType(chatMessage.getMessageType())
                .type(chatMessage.getType())
                .sentAt(chatMessage.getSentAt())
                .build();
    }

    private ChatMessageResponse userEnterMessage(ChatMessage chatMessage) {
        return UserEnterResponsePayload.builder()
                .userId(chatMessage.getUserId())
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .type(chatMessage.getType())
                .build();
    }

    private ChatMessageResponse userLeaveMessage(ChatMessage chatMessage) {
        return UserLeaveResponsePayload.builder()
                .userId(chatMessage.getUserId())
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .type(chatMessage.getType())
                .build();
    }

    private ChatMessageResponse chatroomClosedMessage(ChatMessage chatMessage) {
        return ChatroomClosedResponsePayload.builder()
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .type(chatMessage.getType())
                .build();
    }

    private ChatMessageResponse userChatMessage(ChatMessage chatMessage) {
        return UserChatMessagePayload.builder()
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .senderId(chatMessage.getUserId())
                .photoId(chatMessage.getPhotoId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .unreadBy(unreadChatMessageService.getUserIdsByChatMessage(chatMessage))
                .type(chatMessage.getType())
                .build();
    }
}
