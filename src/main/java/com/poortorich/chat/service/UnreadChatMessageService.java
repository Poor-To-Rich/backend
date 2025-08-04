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
        return chatMembers.stream()
                .map(chatParticipant -> {
                    UnreadChatMessage unreadChatMessage = UnreadChatMessage.builder()
                            .user(chatParticipant.getUser())
                            .chatroom(chatParticipant.getChatroom())
                            .chatMessage(chatMessage)
                            .build();

                    unreadChatMessageRepository.save(unreadChatMessage);

                    return chatParticipant.getUser().getId();
                })
                .toList();
    }
}
