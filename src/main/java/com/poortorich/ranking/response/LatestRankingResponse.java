package com.poortorich.ranking.response;

import com.poortorich.chat.response.ProfileResponse;
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
    private ProfileResponse saver;
    private ProfileResponse flexer;
}
