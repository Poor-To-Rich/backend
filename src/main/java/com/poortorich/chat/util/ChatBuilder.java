package com.poortorich.chat.util;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.user.entity.User;

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
}
