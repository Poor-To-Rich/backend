package com.poortorich.chat.manager;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatroomLeaveManager {

    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;
    private final ChatMessageService chatMessageService;

    public BasePayload leaveChatroom(PayloadContext payloadContext) {
        if (payloadContext.chatroom().getIsDeleted()) {
            return getClosedMessageOrDeleteAll(payloadContext);
        }

        return chatMessageService.saveUserLeaveMessage(payloadContext.user(), payloadContext.chatroom())
                .mapToBasePayload();
    }

    private BasePayload getClosedMessageOrDeleteAll(PayloadContext payloadContext) {
        Chatroom chatroom = payloadContext.chatroom();
        ChatParticipant participant = payloadContext.chatParticipant();

        if (chatParticipantService.isAllParticipantLeft(chatroom)) {
            chatMessageService.deleteAllByChatroom(chatroom);
            chatParticipantService.deleteAllByChatroom(chatroom);
            chatroomService.deleteById(chatroom.getId());
            return null;
        }

        if (participant.getRole().equals(ChatroomRole.HOST)) {
            return chatMessageService.saveChatroomClosedMessage(chatroom).mapToBasePayload();
        }
        return null;
    }
}
