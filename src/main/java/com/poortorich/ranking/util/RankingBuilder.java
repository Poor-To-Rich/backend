package com.poortorich.ranking.util;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.ranking.entity.Ranking;
import com.poortorich.ranking.response.LatestRankingResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingBuilder {

    public static LatestRankingResponse buildNotFoundLatestRankingResponse(LocalDateTime rankedAt) {
        return LatestRankingResponse.builder()
                .rankedAt(rankedAt.toString())
                .rankingId(null)
                .saver(null)
                .flexer(null)
                .build();
    }

    public static LatestRankingResponse buildLatestRankingResponse(
            Ranking latestRanking,
            ChatParticipant saver,
            ChatParticipant flexer
    ) {
        return LatestRankingResponse.builder()
                .rankedAt(latestRanking.getCreatedDate().toString())
                .rankingId(latestRanking.getId())
                .saver(ChatBuilder.buildProfileResponse(saver))
                .flexer(ChatBuilder.buildProfileResponse(flexer))
                .build();
    }
}
