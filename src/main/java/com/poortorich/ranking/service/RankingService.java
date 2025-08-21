package com.poortorich.ranking.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.ranking.entity.Ranking;
import com.poortorich.ranking.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;

    public Ranking findLatestRanking(Chatroom chatroom, LocalDateTime start, LocalDateTime end) {
        return rankingRepository.findByChatroomAndCreatedDateBetween(chatroom, start, end)
                .orElse(null);
    }
}
