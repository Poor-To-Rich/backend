package com.poortorich.chat.realtime.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.manager.ChatroomLeaveManager;
import com.poortorich.chat.realtime.collect.ChatPayloadCollector;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.BasePayload;
import com.poortorich.chat.realtime.payload.UserChatMessagePayload;
import com.poortorich.chat.realtime.payload.UserEnterResponsePayload;
import com.poortorich.chat.realtime.payload.request.ChatMessageRequestPayload;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.service.UnreadChatMessageService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRealTimeFacade {

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final ChatMessageService chatMessageService;
    private final ChatParticipantService chatParticipantService;
    private final UnreadChatMessageService unreadChatMessageService;

    private final ChatPayloadCollector payloadCollector;
    private final ChatroomLeaveManager chatroomLeaveManager;

    public BasePayload createUserEnterSystemMessage(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);

        if (chatroom.getIsDeleted()) {
            return null;
        }

        UserEnterResponsePayload payload = chatMessageService.saveUserEnterMessage(user, chatroom);

        return BasePayload.builder()
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .payload(payload)
                .build();
    }

    public BasePayload createUserLeaveSystemMessage(String username, Long chatroomId) {
        PayloadContext context = payloadCollector.getPayloadContext(username, chatroomId);

        return chatroomLeaveManager.leaveChatroom(context);
    }

    public BasePayload createUserChatMessage(String username, ChatMessageRequestPayload chatMessagePayload) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatMessagePayload.getChatroomId());
        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);

        List<ChatParticipant> chatMembers = chatParticipantService.findAllByChatroom(chatroom).stream()
                .filter(participant -> !participant.getUser().getId().equals(chatParticipant.getUser().getId()))
                .toList();

        UserChatMessagePayload chatMessage = chatMessageService
                .saveUserChatMessage(chatParticipant, chatMembers, chatMessagePayload);

        return BasePayload.builder()
                .type(ChatMessageType.CHAT_MESSAGE)
                .payload(chatMessage)
                .build();
    }
}
