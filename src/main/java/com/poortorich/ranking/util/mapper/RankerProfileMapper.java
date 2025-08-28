package com.poortorich.ranking.util.mapper;

import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.response.ProfileResponse;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.ranking.entity.Ranking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RankerProfileMapper {

    private ChatParticipantService participantService;

    public List<ProfileResponse> mapToSavers(Ranking ranking) {
        if (Objects.isNull(ranking)) {
            return List.of();
        }

        List<Long> savers = List.of(
                ranking.getSaverFirst(),
                ranking.getSaverSecond(),
                ranking.getSaverThird());

        return savers.stream()
                .filter(saverId -> !Objects.isNull(saverId))
                .map(saverId -> participantService.findByUserIdAndChatroom(saverId, ranking.getChatroom()))
                .map(saver -> ProfileResponse.builder()
                        .userId(saver.getUser().getId())
                        .profileImage(saver.getUser().getProfileImage())
                        .nickname(saver.getUser().getNickname())
                        .isHost(ChatroomRole.HOST.equals(saver.getRole()))
                        .rankingType(saver.getRankingStatus().name())
                        .build())
                .toList();
    }

    public List<ProfileResponse> mapToFlexer(Ranking ranking) {
        if (Objects.isNull(ranking)) {
            return List.of();
        }

        List<Long> savers = List.of(
                ranking.getFlexerFirst(),
                ranking.getFlexerSecond(),
                ranking.getFlexerThird());

        return savers.stream()
                .filter(flexerId -> !Objects.isNull(flexerId))
                .map(flexer -> participantService.findByUserIdAndChatroom(flexer, ranking.getChatroom()))
                .map(flexer -> ProfileResponse.builder()
                        .userId(flexer.getUser().getId())
                        .profileImage(flexer.getUser().getProfileImage())
                        .nickname(flexer.getUser().getNickname())
                        .isHost(ChatroomRole.HOST.equals(flexer.getRole()))
                        .rankingType(flexer.getRankingStatus().name())
                        .build())
                .toList();
    }
}
