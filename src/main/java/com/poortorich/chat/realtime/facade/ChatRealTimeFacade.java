package com.poortorich.chat.realtime.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.model.MarkAllChatroomAsReadResult;
import com.poortorich.chat.realtime.collect.ChatPayloadCollector;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.realtime.payload.request.ChatMessageRequestPayload;
import com.poortorich.chat.realtime.payload.request.ChatNoticeRequestPayload;
import com.poortorich.chat.realtime.payload.request.MarkMessagesAsReadRequestPayload;
import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chat.realtime.payload.response.MessageReadPayload;
import com.poortorich.chat.realtime.payload.response.RankingStatusMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserChatMessagePayload;
import com.poortorich.chat.realtime.payload.response.UserEnterResponsePayload;
import com.poortorich.chat.realtime.payload.response.enums.PayloadType;
import com.poortorich.chat.response.MarkAllChatroomAsReadResponse;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.service.UnreadChatMessageService;
import com.poortorich.chat.util.manager.ChatroomLeaveManager;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.service.ChatNoticeService;
import com.poortorich.chatnotice.util.ChatNoticeBuilder;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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

    private final ChatParticipantValidator participantValidator;

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

        List<ChatParticipant> chatMembers = chatParticipantService.findUnreadMembers(chatroom, user);

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
    public BasePayload markMessagesAsRead(String username, MarkMessagesAsReadRequestPayload requestPayload) {
        PayloadContext context = payloadCollector.getPayloadContext(username, requestPayload.getChatroomId());

        MessageReadPayload payload = unreadChatMessageService.markMessageAsRead(context.chatParticipant());

        return payload.mapToBasePayload();
    }

    public MarkAllChatroomAsReadResult markAllChatroomAsRead(String username) {
        List<PayloadContext> contexts = payloadCollector.getAllPayloadContext(username);

        List<Long> chatroomIds = contexts.stream().map(PayloadContext::chatroom).map(Chatroom::getId).toList();

        List<BasePayload> broadcastPayloads = unreadChatMessageService.markAllMessageAsRead(contexts);

        return MarkAllChatroomAsReadResult.builder()
                .apiResponse(MarkAllChatroomAsReadResponse.builder().chatroomIds(chatroomIds).build())
                .broadcastPayloads(broadcastPayloads)
                .build();
    }

    @Transactional
    public BasePayload handleChatNotice(String username, ChatNoticeRequestPayload requestPayload) {
        PayloadContext context = payloadCollector.getPayloadContext(username, requestPayload.getChatroomId());

        participantValidator.validateIsHost(context.chatParticipant());
        ChatNotice chatNotice = chatNoticeService.handleChatNotice(context, requestPayload);

        if (Objects.isNull(chatNotice)) {
            return BasePayload.builder()
                    .type(PayloadType.NOTICE)
                    .payload(null)
                    .build();
        }

        List<ChatParticipant> chatParticipants = chatParticipantService.findAllByChatroom(context.chatroom());
        NoticeStatus noticeStatus = chatParticipantService.updateAllNoticeStatus(
                chatParticipants,
                requestPayload.getNoticeType());
        
        return BasePayload.builder()
                .type(PayloadType.NOTICE)
                .payload(ChatNoticeBuilder.buildLatestNoticeResponse(noticeStatus, chatNotice))
                .build();
    }
}
