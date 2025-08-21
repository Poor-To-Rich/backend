package com.poortorich.ranking.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.ranking.entity.Ranking;
import com.poortorich.ranking.repository.RankingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private RankingRepository rankingRepository;

    @InjectMocks
    private RankingService rankingService;

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime lastMonday = now
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .toLocalDate()
            .atStartOfDay();

    @Test
    @DisplayName("최신 랭킹 조회 성공")
    void findLatestRankingSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        Ranking ranking = Ranking.builder().chatroom(chatroom).build();

        when(rankingRepository.findByChatroomAndCreatedDateBetween(chatroom, lastMonday, now))
                .thenReturn(Optional.of(ranking));

        Ranking result = rankingService.findLatestRanking(chatroom, lastMonday, now);

        assertThat(result).isEqualTo(ranking);
        assertThat(result.getChatroom()).isEqualTo(chatroom);
    }

    @Test
    @DisplayName("최신 랭킹이 없는 경우 null 반환")
    void findLatestRankingNull() {
        Chatroom chatroom = Chatroom.builder().build();

        when(rankingRepository.findByChatroomAndCreatedDateBetween(chatroom, lastMonday, now))
                .thenReturn(Optional.empty());

        Ranking result = rankingService.findLatestRanking(chatroom, lastMonday, now);

        assertThat(result).isNull();
    }
}
