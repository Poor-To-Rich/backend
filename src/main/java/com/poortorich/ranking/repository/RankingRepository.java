package com.poortorich.ranking.repository;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.ranking.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findFirstByChatroomAndCreatedDateBetweenOrderByCreatedDateDesc(
            Chatroom chatroom,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("""
        SELECT r
          FROM Ranking r
         WHERE r.chatroom = :chatroom
           AND r.createdDate IN :mondays
         ORDER BY r.createdDate DESC
    """)
    List<Ranking> findAllByChatroomWithDateIn(Chatroom chatroom, List<LocalDateTime> mondays);
}
