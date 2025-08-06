package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.UnreadChatMessage;
import com.poortorich.chat.repository.UnreadChatMessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
