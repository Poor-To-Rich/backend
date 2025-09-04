package com.poortorich.ranking.util.mapper;

import com.poortorich.chat.response.ChatParticipantProfile;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.util.mapper.ParticipantProfileMapper;
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
    private final ParticipantProfileMapper participantProfileMapper;

    public List<ChatParticipantProfile> mapToSavers(Ranking ranking) {
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
                .map(participantProfileMapper::mapToProfile)
                .toList();
    }

    public List<ChatParticipantProfile> mapToFlexer(Ranking ranking) {
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
                .map(participantProfileMapper::mapToProfile)
                .toList();
    }
}
