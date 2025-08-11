package com.poortorich.chat.realtime.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.realtime.collect.ChatPayloadCollector;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.request.ChatMessageRequestPayload;
import com.poortorich.chat.realtime.payload.request.MarkMessagesAsReadRequestPayload;
import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chat.realtime.payload.response.MessageReadPayload;
import com.poortorich.chat.realtime.payload.response.RankingStatusMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserChatMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserEnterResponsePayload;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.service.UnreadChatMessageService;
import com.poortorich.chat.util.detector.RankingStatusChangeDetector;
import com.poortorich.chat.util.manager.ChatroomLeaveManager;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final RankingStatusChangeDetector rankingStatusChangeDetector;

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
        PayloadContext context = payloadCollector.getPayloadContext(
                username,
                chatMessagePayload.getChatroomId());

        User user = context.user();
        Chatroom chatroom = context.chatroom();

        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);

        List<ChatParticipant> chatMembers = chatParticipantService.findAllByChatroomExcludingUser(chatroom, user);

        UserChatMessagePayload chatMessage = chatMessageService
                .saveUserChatMessage(chatParticipant, chatMembers, chatMessagePayload);

        return chatMessage.mapToBasePayload();
    }

    public BasePayload createRankingStatusMessage(Long chatroomId, Boolean isChangedRankingStatus) {
        if (isChangedRankingStatus) {
            PayloadContext context = payloadCollector.getPayloadContext(chatroomId);
            RankingStatusMessagePayload payload = chatMessageService.saveRankingStatusMessage(context);
            return payload.mapToBasePayload();
        }
        return null;
    }

    @Transactional
    public BasePayload markMessagesAsRead(String username, @Valid MarkMessagesAsReadRequestPayload requestPayload) {
        PayloadContext context = payloadCollector.getPayloadContext(username, requestPayload.getChatroomId());

        MessageReadPayload payload = unreadChatMessageService.markMessageAsRead(context.chatParticipant());

        return payload.mapToBasePayload();
    }
}
