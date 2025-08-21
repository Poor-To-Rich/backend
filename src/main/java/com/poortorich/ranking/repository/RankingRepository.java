package com.poortorich.ranking.repository;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.ranking.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findFirstByChatroomAndCreatedDateBetweenOrderByCreatedDateDesc(
            Chatroom chatroom,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
