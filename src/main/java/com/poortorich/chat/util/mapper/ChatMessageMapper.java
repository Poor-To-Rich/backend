package com.poortorich.chat.util.mapper;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.model.ChatMessageResponse;
import com.poortorich.chat.realtime.payload.response.ChatroomClosedResponsePayload;
import com.poortorich.chat.realtime.payload.response.UserChatMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserEnterResponsePayload;
import com.poortorich.chat.realtime.payload.response.UserLeaveResponsePayload;
import com.poortorich.chat.service.UnreadChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageMapper {

    private final UnreadChatMessageService unreadChatMessageService;

    public ChatMessageResponse mapToChatMessageResponse(ChatMessage chatMessage) {
        return switch (chatMessage.getMessageType()) {
            // TODO: 랭킹 기능 구현 후 추가 예정
            case RANKING -> null;
            case ENTER -> userEnterMessage(chatMessage);
            case LEAVE -> userLeaveMessage(chatMessage);
            case CLOSE -> chatroomClosedMessage(chatMessage);
            case TEXT, PHOTO -> userChatMessage(chatMessage);
        };
    }

    private ChatMessageResponse userEnterMessage(ChatMessage chatMessage) {
        return UserEnterResponsePayload.builder()
                .userId(chatMessage.getUserId())
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sendAt(chatMessage.getSentAt())
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
                .sendAt(chatMessage.getSentAt())
                .type(chatMessage.getType())
                .build();
    }

    private ChatMessageResponse chatroomClosedMessage(ChatMessage chatMessage) {
        return ChatroomClosedResponsePayload.builder()
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sendAt(chatMessage.getSentAt())
                .type(chatMessage.getType())
                .build();
    }

    private ChatMessageResponse userChatMessage(ChatMessage chatMessage) {
        return UserChatMessagePayload.builder()
                .messageId(chatMessage.getId())
                .chatroomId(chatMessage.getChatroom().getId())
                .senderId(chatMessage.getUserId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sendAt(chatMessage.getSentAt())
                .unreadBy(unreadChatMessageService.getUserIdsByChatMessage(chatMessage))
                .build();
    }
}
