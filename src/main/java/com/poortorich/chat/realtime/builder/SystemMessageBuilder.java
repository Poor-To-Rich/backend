package com.poortorich.chat.realtime.builder;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.MessageType;
import com.poortorich.user.entity.User;

public class SystemMessageBuilder {

    private static final String ENTER_CONTENT_SUFFIX = "님이 입장했습니다.";
    private static final String LEAVE_CONTENT_SUFFIX = "님이 퇴장했습니다.";
    private static final String CHATROOM_CLOSED_BY_HOST = "방장이 채팅방을 종료했습니다. 더 이상 대화를 할 수 없으며, 채팅방을 나가면 다시 입장할 수 없게됩니다.";

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

    public static ChatMessage buildChatroomClosedMessage(Chatroom chatroom) {
        return ChatMessage.builder()
                .messageType(MessageType.CLOSE)
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .content(CHATROOM_CLOSED_BY_HOST)
                .chatroom(chatroom)
                .build();
    }
}
