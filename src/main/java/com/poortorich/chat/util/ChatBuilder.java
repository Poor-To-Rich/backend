package com.poortorich.chat.util;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.response.ChatroomInfoResponse;
import com.poortorich.chat.response.ChatroomResponse;
import com.poortorich.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class ChatBuilder {

    public static Chatroom buildChatroom(String imageUrl, ChatroomCreateRequest request) {
        return Chatroom.builder()
                .image(imageUrl)
                .title(request.getChatroomTitle())
                .description(request.getDescription())
                .maxMemberCount(request.getMaxMemberCount())
                .password(request.getChatroomPassword())
                .isRankingEnabled(request.getIsRankingEnabled())
                .build();
    }

    public static ChatParticipant buildChatParticipant(User user, ChatroomRole role, Chatroom chatroom) {
        return ChatParticipant.builder()
                .role(role)
                .isParticipate(true)
                .rankingStatus(RankingStatus.NONE)
                .noticeStatus(NoticeStatus.DEFAULT)
                .user(user)
                .chatroom(chatroom)
                .build();
    }

    public static ChatroomInfoResponse buildChatroomInfoResponse(Chatroom chatroom, List<String> hashtags) {
        return ChatroomInfoResponse.builder()
                .chatroomImage(chatroom.getImage())
                .chatroomTitle(chatroom.getTitle())
                .description(chatroom.getDescription())
                .maxMemberCount(chatroom.getMaxMemberCount())
                .hashtags(hashtags)
                .isRankingEnabled(chatroom.getIsRankingEnabled())
                .chatroomPassword(chatroom.getPassword())
                .build();
    }

    public static ChatroomResponse buildChatroomResponse(
            Chatroom chatroom,
            List<String> hashtags,
            Long currentMemberCount,
            String lastMessageTime
    ) {
        return ChatroomResponse.builder()
                .chatroomId(chatroom.getId())
                .chatroomTitle(chatroom.getTitle())
                .chatroomImage(chatroom.getImage())
                .description(chatroom.getDescription())
                .hashtags(hashtags)
                .currentMemberCount(currentMemberCount)
                .maxMemberCount(chatroom.getMaxMemberCount())
                .lastMessageTime(lastMessageTime)
                .build();
    }
}
