package com.poortorich.ranking.response;

import com.poortorich.chat.response.ChatParticipantProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatestRankingResponse {

    private String rankedAt;
    private Long rankingId;
    private ChatParticipantProfile saver;
    private ChatParticipantProfile flexer;
}
