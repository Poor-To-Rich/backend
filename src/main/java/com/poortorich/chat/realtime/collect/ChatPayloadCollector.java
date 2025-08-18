package com.poortorich.chat.realtime.collect;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.realtime.model.PayloadContext;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatPayloadCollector {

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;

    public PayloadContext getPayloadContext(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);

        return PayloadContext.builder()
                .user(user)
                .chatroom(chatroom)
                .chatParticipant(chatParticipant)
                .build();
    }

    public PayloadContext getPayloadContext(Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        return PayloadContext.builder().chatroom(chatroom).build();
    }

    public List<PayloadContext> getAllPayloadContext(String username) {
        User user = userService.findUserByUsername(username);

        List<ChatParticipant> participants = chatParticipantService.findAllByUser(user);

        return participants.stream()
                .map(participant -> PayloadContext.builder()
                        .user(user)
                        .chatroom(participant.getChatroom())
                        .chatParticipant(participant)
                        .build())
                .toList();
    }
}
