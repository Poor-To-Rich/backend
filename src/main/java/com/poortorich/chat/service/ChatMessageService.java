package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.realtime.builder.SystemMessageBuilder;
import com.poortorich.chat.realtime.builder.UserChatMessageBuilder;
import com.poortorich.chat.realtime.payload.request.ChatMessageRequestPayload;
import com.poortorich.chat.realtime.payload.response.ChatroomClosedResponsePayload;
import com.poortorich.chat.realtime.payload.response.UserChatMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserEnterResponsePayload;
import com.poortorich.chat.realtime.payload.response.UserLeaveResponsePayload;
import com.poortorich.chat.repository.ChatMessageRepository;
import com.poortorich.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
}
