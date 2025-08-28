package com.poortorich.ranking.util.mapper;

import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.response.ProfileResponse;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.ranking.entity.Ranking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RankerProfileMapper {

    private final ChatParticipantService participantService;

    public List<ProfileResponse> mapToSavers(Ranking ranking) {
        if (Objects.isNull(ranking)) {
            return List.of();
        }

        List<Long> savers = Stream.of(
                        ranking.getSaverFirst(),
                        ranking.getSaverSecond(),
                        ranking.getSaverThird())
                .filter(Objects::nonNull)
                .toList();

        return savers.stream()
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

        List<Long> flexers = Stream.of(
                        ranking.getFlexerFirst(),
                        ranking.getFlexerSecond(),
                        ranking.getFlexerThird())
                .filter(Objects::nonNull)
                .toList();

        return flexers.stream()
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
