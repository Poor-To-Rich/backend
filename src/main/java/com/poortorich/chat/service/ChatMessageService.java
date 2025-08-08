package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.model.ChatPaginationContext;
import com.poortorich.chat.realtime.builder.RankingStatusChatMessageBuilder;
import com.poortorich.chat.realtime.builder.SystemMessageBuilder;
import com.poortorich.chat.realtime.builder.UserChatMessageBuilder;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.request.ChatMessageRequestPayload;
import com.poortorich.chat.realtime.payload.response.ChatroomClosedResponsePayload;
import com.poortorich.chat.realtime.payload.response.RankingStatusMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserChatMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserEnterResponsePayload;
import com.poortorich.chat.realtime.payload.response.UserLeaveResponsePayload;
import com.poortorich.chat.repository.ChatMessageRepository;
import com.poortorich.user.entity.User;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UnreadChatMessageService unreadChatMessageService;

    public UserEnterResponsePayload saveUserEnterMessage(User user, Chatroom chatroom) {
        ChatMessage chatMessage = chatMessageRepository.save(SystemMessageBuilder.buildEnterMessage(user, chatroom));

        return UserEnterResponsePayload.builder()
                .userId(user.getId())
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sendAt(chatMessage.getSentAt())
                .build();
    }

    public UserLeaveResponsePayload saveUserLeaveMessage(User user, Chatroom chatroom) {
        ChatMessage chatMessage = chatMessageRepository.save(SystemMessageBuilder.buildLeaveMessage(user, chatroom));

        return UserLeaveResponsePayload.builder()
                .userId(user.getId())
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sendAt(chatMessage.getSentAt())
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
        ChatMessage chatMessage = chatMessageRepository.save(
                UserChatMessageBuilder.buildChatMessage(chatParticipant, chatMessageRequestPayload));

        List<Long> unreadBy = unreadChatMessageService.saveUnreadMember(chatMessage, chatMembers);

        return UserChatMessagePayload.builder()
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .senderId(chatMessage.getUserId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sendAt(chatMessage.getSentAt())
                .unreadBy(unreadBy)
                .build();
    }

    @Transactional
    public void closeAllMessagesByChatroom(Chatroom chatroom) {
        chatMessageRepository.findAllByChatroom(chatroom)
                .forEach(ChatMessage::closeChatroom);
    }

    public ChatroomClosedResponsePayload saveChatroomClosedMessage(Chatroom chatroom) {
        ChatMessage chatMessage = chatMessageRepository.save(SystemMessageBuilder.buildChatroomClosedMessage(chatroom));

        return ChatroomClosedResponsePayload.builder()
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sendAt(chatMessage.getSentAt())
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

        return chatMessageRepository.findByChatroomAndIdLessThanEqualOrderByIdDesc(
                context.chatroom(),
                context.cursor(),
                context.pageRequest());
    }

    public RankingStatusMessagePayload saveRankingStatusMessage(PayloadContext context) {
        ChatMessage rankingStatusChatMessage = RankingStatusChatMessageBuilder.buildRankingStatusMessage(
                context.chatroom());

        ChatMessage savedRankingStatusChatMessage = chatMessageRepository.save(rankingStatusChatMessage);

        return RankingStatusMessagePayload.builder()
                .messageId(savedRankingStatusChatMessage.getId())
                .chatroomId(context.chatroom().getId())
                .isRankingEnabled(savedRankingStatusChatMessage.getIsRankingEnabled())
                .sendAt(savedRankingStatusChatMessage.getSentAt())
                .build();
    }
}
