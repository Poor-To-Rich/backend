package com.poortorich.chat.util;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.response.ChatroomCoverInfoResponse;
import com.poortorich.chat.response.ChatroomDetailsResponse;
import com.poortorich.chat.response.ChatroomInfoResponse;
import com.poortorich.chat.response.ChatroomResponse;
import com.poortorich.chat.response.ProfileResponse;
import com.poortorich.s3.constants.S3Constants;
import com.poortorich.user.entity.User;

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
                .isParticipated(true)
                .rankingStatus(RankingStatus.NONE)
                .noticeStatus(NoticeStatus.DEFAULT)
                .user(user)
                .chatroom(chatroom)
                .build();
    }

    public static ChatroomInfoResponse buildChatroomInfoResponse(Chatroom chatroom, List<String> hashtags) {
        boolean isDefaultImage = S3Constants.DEFAULT_PROFILES.contains(chatroom.getImage());

        String chatroomImage = null;
        if (!isDefaultImage) {
            chatroomImage = chatroom.getImage();
        }

        return ChatroomInfoResponse.builder()
                .chatroomImage(chatroomImage)
                .isDefaultProfile(isDefaultImage)
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

    public static ChatroomDetailsResponse buildChatroomDetailsResponse(Chatroom chatroom, Long currentMemberCount) {
        return ChatroomDetailsResponse.builder()
                .chatroomImage(chatroom.getImage())
                .chatroomTitle(chatroom.getTitle())
                .currentMemberCount(currentMemberCount)
                .isRankingEnabled(chatroom.getIsRankingEnabled())
                .isClosed(chatroom.getIsDeleted())
                .build();
    }

    public static ChatroomCoverInfoResponse buildChatroomCoverInfoResponse(
            Chatroom chatroom,
            List<String> hashtags,
            Long currentMemberCount,
            Boolean isJoined,
            ChatParticipant hostInfo
    ) {
        return ChatroomCoverInfoResponse.builder()
                .chatroomId(chatroom.getId())
                .chatroomTitle(chatroom.getTitle())
                .chatroomImage(chatroom.getImage())
                .description(chatroom.getDescription())
                .hashtags(hashtags)
                .currentMemberCount(currentMemberCount)
                .maxMemberCount(chatroom.getMaxMemberCount())
                .createdAt(chatroom.getCreatedDate().toString())
                .isJoined(isJoined)
                .hasPassword(chatroom.getPassword() != null)
                .hostProfile(buildProfileResponse(hostInfo))
                .build();
    }

    public static ProfileResponse buildProfileResponse(ChatParticipant participantInfo) {
        return ProfileResponse.builder()
                .userId(participantInfo.getUser().getId())
                .profileImage(participantInfo.getUser().getProfileImage())
                .nickname(participantInfo.getUser().getNickname())
                .isHost(participantInfo.getRole().equals(ChatroomRole.HOST))
                .rankingType(participantInfo.getRankingStatus().toString())
                .build();
    }
}
