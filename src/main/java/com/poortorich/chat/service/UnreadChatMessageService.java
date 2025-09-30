package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.UnreadChatMessage;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.model.UnreadChatInfo;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.response.MessageReadPayload;
import com.poortorich.chat.repository.UnreadChatMessageRepository;
import com.poortorich.user.entity.User;
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

    public List<Long> getUserIdsByChatMessage(Long userId, ChatMessage chatMessage) {
        return unreadChatMessageRepository.findAllByChatMessageExcludingChatroomRole(chatMessage, ChatroomRole.BANNED)
                .stream()
                .filter(unreadChatMessage -> !userId.equals(unreadChatMessage.getUser().getId()))
                .map(unreadChatMessage -> unreadChatMessage.getUser().getId())
                .toList();
    }

    @Transactional
    public MessageReadPayload markMessageAsRead(ChatParticipant chatParticipant) {
        unreadChatMessageRepository.markMessagesAsRead(chatParticipant.getChatroom(), chatParticipant.getUser());

        return MessageReadPayload.builder()
                .chatroomId(chatParticipant.getChatroom().getId())
                .userId(chatParticipant.getUser().getId())
                .readAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public List<UnreadChatInfo> markAllMessageAsRead(List<PayloadContext> contexts) {
        List<Chatroom> chatrooms = contexts.stream().map(PayloadContext::chatroom).toList();
        List<User> users = contexts.stream().map(PayloadContext::user).toList();

        List<UnreadChatInfo> unreadChatInfos = unreadChatMessageRepository.findLastUnreadMessageIds(chatrooms, users);
        unreadChatMessageRepository.markAllMessageAsRead(chatrooms, users);

        return unreadChatInfos;
    }

    public Long countByUnreadChatMessage(User user, Chatroom chatroom) {
        return unreadChatMessageRepository.countByUserAndChatroom(user, chatroom);
    }

    @Transactional
    public void deleteAllByChatroom(Chatroom chatroom) {
        unreadChatMessageRepository.deleteByChatroom(chatroom);
    }
}
