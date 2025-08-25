package com.poortorich.chat.util.mapper;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.response.MyChatroom;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.UnreadChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatroomMapper {

    private final UnreadChatMessageService unreadChatMessageService;
    private final ChatMessageService chatMessageService;
    private final ChatParticipantService chatParticipantService;

    public MyChatroom mapToMyChatroom(ChatParticipant participant) {
        Chatroom chatroom = participant.getChatroom();

        ChatMessage lastMessage = chatMessageService.getLastMessage(participant.getChatroom());
        Long unreadMessageCount = unreadChatMessageService.countByUnreadChatMessage(
                participant.getUser(),
                participant.getChatroom());

        return MyChatroom.builder()
                .chatroomId(chatroom.getId())
                .chatroomImage(chatroom.getImage())
                .isHost(ChatroomRole.HOST.equals(participant.getRole()))
                .chatroomTitle(chatroom.getTitle())
                .currentMemberCount(chatParticipantService.countByChatroom(chatroom))
                .lastMessage(lastMessage.getContent())
                .lastMessageTime(lastMessage.getSentAt())
                .unreadMessageCount(unreadMessageCount)
                .build();
    }
}
