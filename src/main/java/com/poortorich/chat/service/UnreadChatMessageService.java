package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.UnreadChatMessage;
import com.poortorich.chat.realtime.payload.response.MessageReadPayload;
import com.poortorich.chat.repository.UnreadChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnreadChatMessageService {

    private final UnreadChatMessageRepository unreadChatMessageRepository;

    public List<Long> saveUnreadMember(ChatMessage chatMessage, List<ChatParticipant> chatMembers) {
        List<UnreadChatMessage> unreadChatMessages = chatMembers.stream()
                .map(chatParticipant -> UnreadChatMessage.builder()
                        .user(chatParticipant.getUser())
                        .chatroom(chatParticipant.getChatroom())
                        .chatMessage(chatMessage)
                        .build())
                .toList();

        unreadChatMessageRepository.saveAll(unreadChatMessages);

        return chatMembers.stream()
                .map(chatParticipant -> chatParticipant.getUser().getId())
                .toList();
    }

    public List<Long> getUserIdsByChatMessage(ChatMessage chatMessage) {
        return unreadChatMessageRepository.findAllByChatMessage(chatMessage).stream()
                .map(unreadChatMessage -> unreadChatMessage.getUser().getId())
                .toList();
    }

    @Transactional
    public MessageReadPayload markMessageAsRead(ChatParticipant chatParticipant) {
        Long lastReadMessageId = unreadChatMessageRepository.findLastUnreadMessageId(
                chatParticipant.getChatroom(),
                chatParticipant.getUser());
        unreadChatMessageRepository.markMessagesAsRead(chatParticipant.getChatroom(), chatParticipant.getUser());

        return MessageReadPayload.builder()
                .chatroomId(chatParticipant.getChatroom().getId())
                .lastReadMessageId(lastReadMessageId)
                .userId(chatParticipant.getUser().getId())
                .readAt(LocalDateTime.now())
                .build();
    }
}
