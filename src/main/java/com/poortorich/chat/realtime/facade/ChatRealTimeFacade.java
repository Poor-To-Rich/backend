package com.poortorich.chat.realtime.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.realtime.payload.ResponsePayload;
import com.poortorich.chat.realtime.payload.UserChatMessagePayload;
import com.poortorich.chat.realtime.payload.UserEnterResponsePayload;
import com.poortorich.chat.realtime.payload.UserLeaveResponsePayload;
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

    public ResponsePayload createUserEnterSystemMessage(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);

        UserEnterResponsePayload payload = chatMessageService.saveUserEnterMessage(user, chatroom);

        return ResponsePayload.builder()
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .payload(payload)
                .build();
    }

    public ResponsePayload createUserLeaveSystemMessage(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);

        UserLeaveResponsePayload payload = chatMessageService.saveUserLeaveMessage(user, chatroom);

        return ResponsePayload.builder()
                .type(ChatMessageType.SYSTEM_MESSAGE)
                .payload(payload)
                .build();
    }

    public ResponsePayload createUserChatMessage(String username, ChatMessageRequestPayload chatMessagePayload) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatMessagePayload.getChatroomId());
        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);

        List<ChatParticipant> chatMembers = chatParticipantService.findAllByChatroom(chatroom).stream()
                .filter(participant -> !participant.getUser().getId().equals(chatParticipant.getUser().getId()))
                .toList();

        UserChatMessagePayload chatMessage = chatMessageService
                .saveUserChatMessage(chatParticipant, chatMembers, chatMessagePayload);

        return ResponsePayload.builder()
                .type(ChatMessageType.CHAT_MESSAGE)
                .payload(chatMessage)
                .build();
    }
}
