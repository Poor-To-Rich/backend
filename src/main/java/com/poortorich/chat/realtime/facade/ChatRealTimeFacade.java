package com.poortorich.chat.realtime.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.model.MarkAllChatroomAsReadResult;
import com.poortorich.chat.realtime.collect.ChatPayloadCollector;
import com.poortorich.chat.realtime.event.chatroom.ChatroomUpdateEvent;
import com.poortorich.chat.realtime.event.chatroom.UserChatroomUpdateEvent;
import com.poortorich.chat.realtime.event.user.HostDelegationEvent;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.request.ChatMessageRequestPayload;
import com.poortorich.chat.realtime.payload.request.MarkMessagesAsReadRequestPayload;
import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chat.realtime.payload.response.ChatroomClosedResponsePayload;
import com.poortorich.chat.realtime.payload.response.DateChangeMessagePayload;
import com.poortorich.chat.realtime.payload.response.HostDelegationMessagePayload;
import com.poortorich.chat.realtime.payload.response.MessageReadPayload;
import com.poortorich.chat.realtime.payload.response.RankingStatusMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserChatMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserEnterResponsePayload;
import com.poortorich.chat.response.MarkAllChatroomAsReadResponse;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.service.UnreadChatMessageService;
import com.poortorich.chat.util.manager.ChatroomLeaveManager;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.chat.validator.ChatroomValidator;
import com.poortorich.chatnotice.service.ChatNoticeService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRealTimeFacade {

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final ChatMessageService chatMessageService;
    private final ChatParticipantService chatParticipantService;
    private final UnreadChatMessageService unreadChatMessageService;
    private final ChatNoticeService chatNoticeService;
    private final ChatPayloadCollector payloadCollector;
    private final ChatroomLeaveManager chatroomLeaveManager;

    private final ChatroomValidator chatroomValidator;
    private final ChatParticipantValidator participantValidator;

    private final ApplicationEventPublisher eventPublisher;

    public BasePayload createUserEnterSystemMessage(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);

        if (chatroom.getIsClosed()) {
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
        if (ChatroomRole.BANNED.equals(context.chatParticipant().getRole())) {
            return null;
        }
        return chatMessageService.saveUserLeaveMessage(context.user(), context.chatroom()).mapToBasePayload();
    }

    public ChatroomClosedResponsePayload createChatroomClosedMessageOrDeleteAll(String username, Long chatroomId) {
        PayloadContext context = payloadCollector.getPayloadContext(username, chatroomId);
        return chatroomLeaveManager.leaveChatroom(context.chatParticipant());
    }

    @Transactional
    public BasePayload createUserChatMessage(String username, ChatMessageRequestPayload chatMessagePayload) {
        PayloadContext context = payloadCollector.getPayloadContext(
                username,
                chatMessagePayload.getChatroomId());

        User user = context.user();
        Chatroom chatroom = context.chatroom();

        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);

        chatroomValidator.validateIsOpened(chatroom);
        participantValidator.validateIsParticipate(chatParticipant);
        participantValidator.validateIsBanned(chatParticipant);

        List<ChatParticipant> chatMembers = chatParticipantService.findUnreadMembers(chatroom, user);

        UserChatMessagePayload chatMessage = chatMessageService
                .saveUserChatMessage(chatParticipant, chatMembers, chatMessagePayload);

        eventPublisher.publishEvent(new ChatroomUpdateEvent(chatroom));

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
    public BasePayload markMessagesAsRead(String username, MarkMessagesAsReadRequestPayload requestPayload) {
        PayloadContext context = payloadCollector.getPayloadContext(username, requestPayload.getChatroomId());

        MessageReadPayload payload = unreadChatMessageService.markMessageAsRead(context.chatParticipant());

        return payload.mapToBasePayload();
    }

    public BasePayload createDateChangeSystemMessage(Long newChatroomId) {
        PayloadContext context = payloadCollector.getPayloadContext(newChatroomId);

        DateChangeMessagePayload payload = chatMessageService.saveDateChangeMessage(context.chatroom());

        return payload.mapToBasePayload();
    }

    public MarkAllChatroomAsReadResult markAllChatroomAsRead(String username) {
        List<PayloadContext> contexts = payloadCollector.getAllPayloadContext(username);

        List<MessageReadPayload> broadcastPayloads = unreadChatMessageService.markAllMessageAsRead(contexts);

        eventPublisher.publishEvent(new UserChatroomUpdateEvent(contexts.getFirst().user()));
        List<Long> chatroomIds = broadcastPayloads.stream()
                .map(MessageReadPayload::getChatroomId)
                .toList();

        return MarkAllChatroomAsReadResult.builder()
                .apiResponse(MarkAllChatroomAsReadResponse.builder().chatroomIds(chatroomIds).build())
                .broadcastPayloads(broadcastPayloads)
                .build();

    }

    @Transactional
    public BasePayload createHostDelegationMessage(ChatParticipant prevHost, ChatParticipant newHost) {
        HostDelegationMessagePayload payload = chatMessageService.saveHostDelegationMessage(prevHost, newHost);
        eventPublisher.publishEvent(new HostDelegationEvent(prevHost, newHost));
        return payload.mapToBasePayload();
    }

    @Transactional
    public BasePayload createUserKickMessage(ChatParticipant kickChatParticipant) {
        return chatMessageService.saveKickChatParticipantMessage(kickChatParticipant).mapToBasePayload();
    }
}
