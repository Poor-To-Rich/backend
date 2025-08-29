package com.poortorich.ranking.response;

import com.poortorich.chat.response.ProfileResponse;
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
    private String rankingAt;
    private List<ProfileResponse> saverRankings;
    private List<ProfileResponse> flexerRankings;
}
