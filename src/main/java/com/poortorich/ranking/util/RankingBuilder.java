package com.poortorich.ranking.util;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.util.mapper.ParticipantProfileMapper;
import com.poortorich.ranking.entity.Ranking;
import com.poortorich.ranking.response.LatestRankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RankingBuilder {

    private final ParticipantProfileMapper profileMapper;

    public LatestRankingResponse buildLatestRankingResponse(
            Ranking latestRanking,
            ChatParticipant saver,
            ChatParticipant flexer
    ) {
        return LatestRankingResponse.builder()
                .rankedAt(latestRanking.getCreatedDate().toString())
                .rankingId(latestRanking.getId())
                .saver(profileMapper.mapToProfile(saver))
                .flexer(profileMapper.mapToProfile(flexer))
                .build();
    }

    public LatestRankingResponse buildNotFoundLatestRankingResponse(LocalDateTime rankedAt) {
        return LatestRankingResponse.builder()
                .rankedAt(rankedAt.toString())
                .rankingId(null)
                .saver(null)
                .flexer(null)
                .build();
    }
}
