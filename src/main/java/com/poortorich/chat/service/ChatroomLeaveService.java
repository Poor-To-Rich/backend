package com.poortorich.chat.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.realtime.event.chatroom.ChatroomUpdateEvent;
import com.poortorich.chat.realtime.payload.response.ChatroomClosedResponsePayload;
import com.poortorich.chat.realtime.payload.response.UserLeaveResponsePayload;
import com.poortorich.chat.realtime.payload.response.enums.PayloadType;
import com.poortorich.chat.util.manager.ChatroomLeaveManager;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.tag.service.TagService;
import com.poortorich.user.entity.User;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatroomLeaveService {

    private final TagService tagService;
    private final ChatMessageService chatMessageService;
    private final ChatroomService chatroomService;
    private final ChatParticipantService participantService;

    private final ChatroomLeaveManager leaveManager;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatParticipantValidator participantValidator;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void leaveAllChatroom(User user) {
        List<ChatParticipant> participants = participantService.findAllByUser(user);
        for (ChatParticipant participant : participants) {
            leaveChatroom(participant);
        }
    }

    @Transactional
    public void leaveChatroom(ChatParticipant participant) {
        participantValidator.validateIsParticipate(participant);
        participant.updateNoticeStatus(NoticeStatus.DEFAULT);
        participant.leave();
        if (ChatroomRole.HOST.equals(participant.getRole())) {
            deleteChatroom(participant.getChatroom());
        }
        eventPublisher.publishEvent(new ChatroomUpdateEvent(
                participant.getChatroom(),
                PayloadType.CHATROOM_INFO_UPDATED));

        if (!ChatroomRole.BANNED.equals(participant.getRole())) {
            saveLeaveMessageAndBroadcast(participant.getUser(), participant.getChatroom());
            saveCloseMessageAndBroadcast(participant);
        }
    }

    @Transactional
    protected void saveLeaveMessageAndBroadcast(User user, Chatroom chatroom) {
        UserLeaveResponsePayload payload = chatMessageService.saveUserLeaveMessage(user, chatroom);

        if (Objects.nonNull(payload)) {
            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroom.getId(),
                    payload.mapToBasePayload());
        }
    }

    protected void saveCloseMessageAndBroadcast(ChatParticipant participant) {
        ChatroomClosedResponsePayload payload = leaveManager.leaveChatroom(participant);

        if (Objects.nonNull(payload)) {
            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + participant.getChatroom().getId(),
                    payload.mapToBasePayload());
        }
    }

    @Transactional
    protected void deleteChatroom(Chatroom chatroom) {
        tagService.deleteAllByChatroom(chatroom);
        chatMessageService.closeAllMessagesByChatroom(chatroom);
        chatroomService.closeChatroomById(chatroom.getId());
    }
}
