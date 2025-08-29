package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.model.ChatPaginationContext;
import com.poortorich.chat.realtime.builder.RankingMessageBuilder;
import com.poortorich.chat.realtime.builder.RankingStatusChatMessageBuilder;
import com.poortorich.chat.realtime.builder.SystemMessageBuilder;
import com.poortorich.chat.realtime.builder.UserChatMessageBuilder;
import com.poortorich.chat.realtime.event.datechange.detector.DateChangeDetector;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.request.ChatMessageRequestPayload;
import com.poortorich.chat.realtime.payload.response.ChatroomClosedResponsePayload;
import com.poortorich.chat.realtime.payload.response.DateChangeMessagePayload;
import com.poortorich.chat.realtime.payload.response.HostDelegationMessagePayload;
import com.poortorich.chat.realtime.payload.response.KickChatParticipantMessagePayload;
import com.poortorich.chat.realtime.payload.response.RankingStatusMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserChatMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserEnterResponsePayload;
import com.poortorich.chat.realtime.payload.response.UserLeaveResponsePayload;
import com.poortorich.chat.repository.ChatMessageRepository;
import com.poortorich.ranking.entity.Ranking;
import com.poortorich.ranking.payload.response.RankingResponsePayload;
import com.poortorich.ranking.util.mapper.RankerProfileMapper;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UnreadChatMessageService unreadChatMessageService;

    private final RankerProfileMapper rankerProfileMapper;
    private final UserChatMessageBuilder userChatMessageBuilder;
    private final DateChangeDetector dateChangeDetector;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserEnterResponsePayload saveUserEnterMessage(User user, Chatroom chatroom) {
        dateChangeDetector.detect(chatroom);
        ChatMessage chatMessage = chatMessageRepository.save(SystemMessageBuilder.buildEnterMessage(user, chatroom));

        return UserEnterResponsePayload.builder()
                .userId(user.getId())
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .type(chatMessage.getType())
                .build();
    }

    @Transactional
    public UserLeaveResponsePayload saveUserLeaveMessage(User user, Chatroom chatroom) {
        dateChangeDetector.detect(chatroom);
        ChatMessage chatMessage = chatMessageRepository.save(SystemMessageBuilder.buildLeaveMessage(user, chatroom));

        return UserLeaveResponsePayload.builder()
                .userId(user.getId())
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .type(chatMessage.getType())
                .build();
    }

    public String getLastMessageTime(Chatroom chatroom) {
        return chatMessageRepository.findTopByChatroomOrderBySentAtDesc(chatroom)
                .map(chatMessage -> chatMessage.getSentAt().toString())
                .orElse(null);
    }

    @Transactional
    public UserChatMessagePayload saveUserChatMessage(
            ChatParticipant chatParticipant,
            List<ChatParticipant> chatMembers,
            ChatMessageRequestPayload chatMessageRequestPayload
    ) {
        dateChangeDetector.detect(chatParticipant.getChatroom());
        ChatMessage chatMessage = chatMessageRepository.save(
                userChatMessageBuilder.buildChatMessage(chatParticipant, chatMessageRequestPayload));

        List<Long> unreadBy = unreadChatMessageService.saveUnreadMember(chatMessage, chatMembers);

        return UserChatMessagePayload.builder()
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .senderId(chatMessage.getUserId())
                .photoId(chatMessage.getPhotoId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .unreadBy(unreadBy)
                .type(chatMessage.getType())
                .build();
    }

    @Transactional
    public DateChangeMessagePayload saveDateChangeMessage(Chatroom chatroom) {
        ChatMessage dateChangeMessage = SystemMessageBuilder.buildDateChangeMessage(chatroom);

        if (chatMessageRepository.existsByContentAndMessageTypeAndChatroom(
                dateChangeMessage.getContent(),
                dateChangeMessage.getMessageType(),
                chatroom)
        ) {
            return null;
        }

        dateChangeMessage = chatMessageRepository.save(dateChangeMessage);
        return DateChangeMessagePayload.builder()
                .messageId(dateChangeMessage.getId())
                .chatroomId(dateChangeMessage.getChatroom().getId())
                .messageType(dateChangeMessage.getMessageType())
                .content(dateChangeMessage.getContent())
                .sentAt(dateChangeMessage.getSentAt())
                .type(dateChangeMessage.getType())
                .build();
    }

    @Transactional
    public void closeAllMessagesByChatroom(Chatroom chatroom) {
        chatMessageRepository.findAllByChatroom(chatroom)
                .forEach(ChatMessage::closeChatroom);
    }

    @Transactional
    public ChatroomClosedResponsePayload saveChatroomClosedMessage(Chatroom chatroom) {
        dateChangeDetector.detect(chatroom);
        ChatMessage chatMessage = chatMessageRepository.save(SystemMessageBuilder.buildChatroomClosedMessage(chatroom));

        return ChatroomClosedResponsePayload.builder()
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .type(chatMessage.getType())
                .build();
    }

    @Transactional
    public void deleteAllByChatroom(Chatroom chatroom) {
        chatMessageRepository.deleteAllByChatroom(chatroom);
    }

    public Long getLatestMessageId(Chatroom chatroom) {
        return chatMessageRepository.findTopByChatroomOrderBySentAtDesc(chatroom)
                .map(ChatMessage::getId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Slice<ChatMessage> getChatMessages(ChatPaginationContext context) {
        if (Objects.isNull(context.cursor())) {
            return new SliceImpl<>(Collections.emptyList(), context.pageRequest(), false);
        }

        return chatMessageRepository.findByChatroomAndIdLessThanEqualAndSentAtAfterOrderByIdDesc(
                context.chatroom(),
                context.cursor(),
                context.chatParticipant().getJoinAt(),
                context.pageRequest());
    }

    @Transactional
    public RankingStatusMessagePayload saveRankingStatusMessage(PayloadContext context) {
        dateChangeDetector.detect(context.chatroom());
        ChatMessage rankingStatusChatMessage = RankingStatusChatMessageBuilder.buildRankingStatusMessage(
                context.chatroom());

        ChatMessage savedRankingStatusChatMessage = chatMessageRepository.save(rankingStatusChatMessage);

        return RankingStatusMessagePayload.builder()
                .messageId(savedRankingStatusChatMessage.getId())
                .chatroomId(context.chatroom().getId())
                .isRankingEnabled(savedRankingStatusChatMessage.getIsRankingEnabled())
                .sentAt(savedRankingStatusChatMessage.getSentAt())
                .messageType(savedRankingStatusChatMessage.getMessageType())
                .type(savedRankingStatusChatMessage.getType())
                .build();
    }

    public HostDelegationMessagePayload saveHostDelegationMessage(ChatParticipant prevHost, ChatParticipant newHost) {
        dateChangeDetector.detect(newHost.getChatroom());
        ChatMessage hostDelegationMessage = SystemMessageBuilder.buildHostDelegationMessage(prevHost, newHost);

        ChatMessage savedHostDelegationMessage = chatMessageRepository.save(hostDelegationMessage);

        return HostDelegationMessagePayload.builder()
                .messageId(savedHostDelegationMessage.getId())
                .chatroomId(savedHostDelegationMessage.getChatroom().getId())
                .messageType(savedHostDelegationMessage.getMessageType())
                .type(savedHostDelegationMessage.getType())
                .content(savedHostDelegationMessage.getContent())
                .sentAt(savedHostDelegationMessage.getSentAt())
                .build();
    }

    @Transactional
    public KickChatParticipantMessagePayload saveKickChatParticipantMessage(ChatParticipant kickChatParticipant) {
        dateChangeDetector.detect(kickChatParticipant.getChatroom());

        ChatMessage kickMessage = SystemMessageBuilder.buildKickChatParticipantMessage(kickChatParticipant);
        kickMessage = chatMessageRepository.save(kickMessage);

        return KickChatParticipantMessagePayload.builder()
                .userId(kickMessage.getUserId())
                .messageId(kickMessage.getId())
                .chatroomId(kickMessage.getChatroom().getId())
                .content(kickMessage.getContent())
                .messageType(kickMessage.getMessageType())
                .type(kickMessage.getType())
                .sentAt(kickMessage.getSentAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Optional<ChatMessage> getLastMessage(Chatroom chatroom) {
        return chatMessageRepository.findTopByChatroomAndTypeInOrderBySentAtDesc(
                chatroom,
                List.of(ChatMessageType.CHAT_MESSAGE, ChatMessageType.RANKING_MESSAGE));
    }

    @Transactional
    public RankingResponsePayload saveRankingMessage(Chatroom chatroom, Ranking ranking) {
        dateChangeDetector.detect(chatroom);

        ChatMessage rankingMessage = RankingMessageBuilder.buildRankingMessage(chatroom, ranking);
        rankingMessage = chatMessageRepository.save(rankingMessage);

        return RankingResponsePayload.builder()
                .messageId(rankingMessage.getId())
                .rankingId(rankingMessage.getRankingId())
                .chatroomId(rankingMessage.getChatroom().getId())
                .rankedAt(rankingMessage.getSentAt().toLocalDate())
                .sentAt(rankingMessage.getSentAt())
                .saverRankings(rankerProfileMapper.mapToSavers(ranking))
                .flexerRankings(rankerProfileMapper.mapToFlexer(ranking))
                .type(rankingMessage.getType())
                .messageType(rankingMessage.getMessageType())
                .build();
    }

    // TODO: 테스트 이후 삭제
    @Transactional
    public RankingResponsePayload saveRankingMessage(Chatroom chatroom, Ranking ranking, LocalDate date) {
        dateChangeDetector.detect(chatroom);

        ChatMessage rankingMessage = RankingMessageBuilder.buildRankingMessage(chatroom, ranking);
        rankingMessage = chatMessageRepository.save(rankingMessage);
        rankingMessage.updateSentAt(date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        
        return RankingResponsePayload.builder()
                .messageId(rankingMessage.getId())
                .rankingId(rankingMessage.getRankingId())
                .chatroomId(rankingMessage.getChatroom().getId())
                .rankedAt(rankingMessage.getSentAt().toLocalDate())
                .sentAt(rankingMessage.getSentAt())
                .saverRankings(rankerProfileMapper.mapToSavers(ranking))
                .flexerRankings(rankerProfileMapper.mapToFlexer(ranking))
                .type(rankingMessage.getType())
                .messageType(rankingMessage.getMessageType())
                .build();
    }
}
