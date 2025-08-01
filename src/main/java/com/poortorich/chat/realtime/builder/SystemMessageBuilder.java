package com.poortorich.chat.realtime.builder;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.MessageType;
import com.poortorich.user.entity.User;

public class SystemMessageBuilder {

    private static final String ENTER_CONTENT_SUFFIX = "님이 입장했습니다.";
    private static final String LEAVE_CONTENT_SUFFIX = "님이 퇴장했습니다.";

    public static ChatMessage buildEnterMessage(User user, Chatroom chatroom) {
        return ChatMessage.builder()
                .messageType(MessageType.ENTER)
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .content(user.getNickname() + ENTER_CONTENT_SUFFIX)
                .chatroom(chatroom)
                .build();
    }

    public static ChatMessage buildLeaveMessage(User user, Chatroom chatroom) {
        return ChatMessage.builder()
                .messageType(MessageType.LEAVE)
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .content(user.getNickname() + LEAVE_CONTENT_SUFFIX)
                .chatroom(chatroom)
                .build();
    }
}
