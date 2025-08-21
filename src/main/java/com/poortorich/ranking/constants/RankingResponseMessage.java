package com.poortorich.ranking.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingResponseMessage {

    public static final String GET_LATEST_RANKING_SUCCESS = "최신 랭킹 조회에 완료했습니다.";
    public static final String GET_LATEST_RANKING_NOT_FOUND = "랭킹이 집계되지 않았습니다.";
}
