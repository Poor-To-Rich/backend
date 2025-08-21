package com.poortorich.chat.realtime.event.user;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.realtime.payload.response.UserUpdatedResponsePayload;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.user.entity.User;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserProfileUpdatedEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatParticipantService chatParticipantService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserProfileUpdated(UserProfileUpdateEvent event) {
        List<ChatParticipant> participants = chatParticipantService.findAllByUsernameWithChatroomAndUser(
                event.getUsername());

        participants.forEach(participant -> {
            User user = participant.getUser();
            Chatroom chatroom = participant.getChatroom();

            var payload = UserUpdatedResponsePayload.builder()
                    .userId(participant.getUser().getId())
                    .profileImage(user.getProfileImage())
                    .nickname(user.getNickname())
                    .isHost(Objects.equals(ChatroomRole.HOST, participant.getRole()))
                    .rankingType(participant.getRankingStatus())
                    .build();

            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroom.getId(),
                    payload.mapToBasePayload());
        });
    }

    @EventListener
    public void onHostDelegation(HostDelegationEvent event) {
        List<ChatParticipant> participants = List.of(event.getPrevHost(), event.getNewHost());

        participants.forEach(participant -> {
            User user = participant.getUser();
            Chatroom chatroom = participant.getChatroom();

            var payload = UserUpdatedResponsePayload.builder()
                    .userId(user.getId())
                    .profileImage(user.getProfileImage())
                    .nickname(user.getNickname())
                    .isHost(Objects.equals(ChatroomRole.HOST, participant.getRole()))
                    .rankingType(participant.getRankingStatus())
                    .build();

            messagingTemplate.convertAndSend(
                    SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroom.getId(),
                    payload.mapToBasePayload()
            );
        });
    }
}
