package com.poortorich.ranking.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.realtime.event.user.UserProfileUpdateEvent;
import com.poortorich.ranking.entity.Ranking;
import com.poortorich.ranking.model.Rankers;
import com.poortorich.ranking.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Ranking findLatestRanking(Chatroom chatroom, LocalDateTime start, LocalDateTime end) {
        return rankingRepository.findFirstByChatroomAndCreatedDateBetweenOrderByCreatedDateDesc(chatroom, start, end)
                .orElse(null);
    }

    public Ranking create(Chatroom chatroom, Rankers rankers) {
        if (rankers == null) {
            return null;
        }

        Ranking ranking = Ranking.builder()
                .chatroom(chatroom)
                .saverFirst(rankers.firstSaver())
                .saverSecond(rankers.secondSaver())
                .saverThird(rankers.thirdSaver())
                .flexerFirst(rankers.firstFlexer())
                .flexerSecond(rankers.secondFlexer())
                .flexerThird(rankers.thirdFlexer())
                .build();

        return rankingRepository.save(ranking);
    }

    @Transactional
    public void updateAll(List<ChatParticipant> participants, RankingStatus rankingStatus) {
        participants.forEach(participant -> participant.updateRankingStatus(rankingStatus));
    }

    @Transactional
    public void updateRankingStatus(Rankers rankers) {
        if (Objects.isNull(rankers)) {
            return;
        }

        if (!Objects.isNull(rankers.firstSaver())) {
            rankers.getSavers().getFirst().updateRankingStatus(RankingStatus.SAVER);
            eventPublisher.publishEvent(new UserProfileUpdateEvent(rankers.getSavers()
                    .getFirst()
                    .getUser()
                    .getUsername()));
        }

        if (!Objects.isNull(rankers.firstFlexer())) {
            rankers.getFlexers().getFirst().updateRankingStatus(RankingStatus.FLEXER);
            eventPublisher.publishEvent(new UserProfileUpdateEvent(rankers.getFlexers()
                    .getFirst()
                    .getUser()
                    .getUsername()));
        }
    }

    public Ranking findById(Long rankingId) {
        if (Objects.isNull(rankingId)) {
            return null;
        }

        return rankingRepository.findById(rankingId)
                .orElse(null);
    }

    public List<Ranking> findAllRankings(Chatroom chatroom, List<LocalDate> mondays) {
        return rankingRepository.findAllByChatroomWithDateIn(chatroom, mondays);
    }

    @Transactional
    public void deleteAllByChatroom(Chatroom chatroom) {
        rankingRepository.deleteByChatroom(chatroom);
    }
}
