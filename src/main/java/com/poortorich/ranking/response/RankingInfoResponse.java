package com.poortorich.ranking.response;

import com.poortorich.chat.response.ChatParticipantProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingInfoResponse {

    private Long rankingId;
    private String rankedAt;
    private List<ChatParticipantProfile> saverRankings;
    private List<ChatParticipantProfile> flexerRankings;
}
