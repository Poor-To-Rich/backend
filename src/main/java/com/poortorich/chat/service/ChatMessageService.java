package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.model.ChatPaginationContext;
import com.poortorich.chat.realtime.builder.RankingStatusChatMessageBuilder;
import com.poortorich.chat.realtime.builder.SystemMessageBuilder;
import com.poortorich.chat.realtime.builder.UserChatMessageBuilder;
import com.poortorich.chat.realtime.event.datechange.detector.DateChangeDetector;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.request.ChatMessageRequestPayload;
import com.poortorich.chat.realtime.payload.response.ChatroomClosedResponsePayload;
import com.poortorich.chat.realtime.payload.response.DateChangeMessagePayload;
import com.poortorich.chat.realtime.payload.response.RankingStatusMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserChatMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserEnterResponsePayload;
import com.poortorich.chat.realtime.payload.response.UserLeaveResponsePayload;
import com.poortorich.chat.repository.ChatMessageRepository;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UnreadChatMessageService unreadChatMessageService;

    private final DateChangeDetector dateChangeDetector;

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
                UserChatMessageBuilder.buildChatMessage(chatParticipant, chatMessageRequestPayload));

        List<Long> unreadBy = unreadChatMessageService.saveUnreadMember(chatMessage, chatMembers);

        return UserChatMessagePayload.builder()
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .senderId(chatMessage.getUserId())
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

        return chatMessageRepository.findByChatroomAndIdLessThanEqualAndCreatedDateAfterOrderByIdDesc(
                context.chatroom(),
                context.cursor(),
                context.chatParticipant().getCreatedDate(),
                context.pageRequest());
    }

    @Transactional
    public RankingStatusMessagePayload saveRankingStatusMessage(PayloadContext context) {
        ChatMessage rankingStatusChatMessage = RankingStatusChatMessageBuilder.buildRankingStatusMessage(
                context.chatroom());

        dateChangeDetector.detect(context.chatroom());
        ChatMessage savedRankingStatusChatMessage = chatMessageRepository.save(rankingStatusChatMessage);

        return RankingStatusMessagePayload.builder()
                .messageId(savedRankingStatusChatMessage.getId())
                .chatroomId(context.chatroom().getId())
                .isRankingEnabled(savedRankingStatusChatMessage.getIsRankingEnabled())
                .sentAt(savedRankingStatusChatMessage.getSentAt())
                .build();
    }
}
