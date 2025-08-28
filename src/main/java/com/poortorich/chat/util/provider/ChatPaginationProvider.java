package com.poortorich.chat.util.provider;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.model.ChatPaginationContext;
import com.poortorich.chat.model.ChatroomPaginationContext;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ChatPaginationProvider {

    private final ChatroomService chatroomService;
    private final ChatMessageService chatMessageService;
    private final ChatParticipantService chatParticipantService;
    private final UserService userService;

    public ChatPaginationContext getChatMessagesContext(String username, Long chatroomId, Long cursor, Long pageSize) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatParticipant chatParticipant = chatParticipantService.findByUserAndChatroom(user, chatroom);
        return ChatPaginationContext.builder()
                .chatroom(chatroom)
                .chatParticipant(chatParticipant)
                .cursor(getCursor(chatroom, cursor))
                .pageRequest(getPageRequest(pageSize))
                .build();
    }

    public ChatroomPaginationContext getMyChatroomsContext(String username, Long cursor) {
        User user = userService.findUserByUsername(username);

        return ChatroomPaginationContext.builder()
                .user(user)
                .cursor(getChatroomCursor(user, cursor))
                .pageRequest(getPageRequest(20L))
                .build();
    }

    private Long getChatroomCursor(User user, Long cursor) {
        if (!Objects.isNull(cursor)) {
            return cursor;
        }

        return chatroomService.getFirstChatroomIdByUser(user);
    }

    private Long getCursor(Chatroom chatroom, Long cursor) {
        if (!Objects.isNull(cursor)) {
            return cursor;
        }

        return chatMessageService.getLatestMessageId(chatroom);
    }


    public Long getNextCursor(Slice<ChatMessage> chatMessages) {
        if (chatMessages.hasContent()) {
            return chatMessages.getContent().getLast().getId() - 1;
        }
        return null;
    }

    public Long getChatroomNextCursor(Slice<ChatParticipant> participants) {
        if (participants.hasContent()) {
            return participants.getContent().getLast().getChatroom().getId() + 1;
        }
        return null;
    }

    private PageRequest getPageRequest(Long pageSize) {
        return PageRequest.of(0, pageSize.intValue());
    }
}
