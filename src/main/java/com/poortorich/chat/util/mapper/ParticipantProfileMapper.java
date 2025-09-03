package com.poortorich.chat.util.mapper;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.response.ChatParticipantProfile;
import com.poortorich.s3.constants.S3Constants;
import com.poortorich.user.entity.User;
import com.poortorich.user.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParticipantProfileMapper {

    private static final String WITHDRAW_USER_NICKNAME = "알 수 없음";

    public ChatParticipantProfile mapToProfile(ChatParticipant participant) {
        User user = participant.getUser();

        if (Role.WITHDRAW.equals(user.getRole())) {
            return mapToWithdrawUserProfile(participant);
        }

        return ChatParticipantProfile.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .rankingType(participant.getRankingStatus())
                .isHost(ChatroomRole.HOST.equals(participant.getRole()))
                .build();
    }

    private ChatParticipantProfile mapToWithdrawUserProfile(ChatParticipant participant) {
        return ChatParticipantProfile.builder()
                .userId(participant.getUser().getId())
                .nickname(WITHDRAW_USER_NICKNAME)
                .profileImage(S3Constants.DEFAULT_PROFILE_IMAGE)
                .rankingType(RankingStatus.NONE)
                .isHost(Boolean.FALSE)
                .build();
    }
}
