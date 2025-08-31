package com.poortorich.chat.util.mapper;

import com.poortorich.chat.entity.ChatMessage;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.enums.ChatroomRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ChatMessageContentMapper {

    private static final String RANKING_MESSAGE = "랭킹이 집계되었습니다.";
    private static final String PHOTO_MESSAGE = "사진을 보냈습니다.";
    private static final String EMPTY_MESSAGE = "";
    private static final String CHATROOM_CREATED_MESSAGE = "새로운 채팅방이 생성되었습니다.";

    public String mapToContent(ChatMessage chatMessage) {
        if (Objects.isNull(chatMessage) || Objects.isNull(chatMessage.getMessageType())) {
            return null;
        }

        return switch (chatMessage.getMessageType()) {
            case PHOTO -> PHOTO_MESSAGE;
            case TEXT -> chatMessage.getContent();
            case RANKING -> RANKING_MESSAGE;
            default -> null;
        };
    }

    public String mapToContent(ChatParticipant participant) {
        if (ChatroomRole.HOST.equals(participant.getRole())) {
            return CHATROOM_CREATED_MESSAGE;
        }
        return EMPTY_MESSAGE;
    }

    public LocalDateTime mapToTime(ChatParticipant participant) {
        if (ChatroomRole.HOST.equals(participant.getRole())) {
            return participant.getChatroom().getCreatedDate();
        }
        return participant.getJoinAt();
    }
}
