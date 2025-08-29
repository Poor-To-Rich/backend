package com.poortorich.ranking.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllRankingsResponse {

    private Boolean hasNext;
    private String nextCursor;
    private List<RankingInfoResponse> rankings;
}
