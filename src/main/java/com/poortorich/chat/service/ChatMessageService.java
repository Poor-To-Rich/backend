package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.realtime.builder.SystemMessageBuilder;
import com.poortorich.chat.realtime.payload.ChatroomClosedResponsePayload;
import com.poortorich.chat.realtime.payload.UserEnterResponsePayload;
import com.poortorich.chat.realtime.payload.UserLeaveResponsePayload;
import com.poortorich.chat.repository.ChatMessageRepository;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

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
}
