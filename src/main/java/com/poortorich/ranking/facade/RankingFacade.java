package com.poortorich.ranking.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.realtime.event.user.UserProfileUpdateEvent;
import com.poortorich.chat.service.ChatMessageService;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.ranking.entity.Ranking;
import com.poortorich.ranking.model.Rankers;
import com.poortorich.ranking.payload.response.RankingResponsePayload;
import com.poortorich.ranking.response.LatestRankingResponse;
import com.poortorich.ranking.service.RankingService;
import com.poortorich.ranking.util.RankingBuilder;
import com.poortorich.ranking.util.calculator.RankingCalculator;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RankingFacade {

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;
    private final ChatParticipantValidator chatParticipantValidator;
    private final RankingService rankingService;

    private final RankingCalculator rankingCalculator;
    private final ChatMessageService chatMessageService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public RankingResponsePayload calculateRankingTest(Long chatroomId) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        return calculateRanking(chatroom);
    }

    // TODO: 테스트 이후 삭제
    @Transactional
    public RankingResponsePayload calculateRankingTest(Long chatroomId, LocalDate date) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        return calculateRanking(chatroom, date);
    }

    // TODO: 테스트 이후 삭제
    @Transactional
    public RankingResponsePayload calculateRanking(Chatroom chatroom, LocalDate date) {
        Rankers rankers = rankingCalculator.calculate(chatroom, date);
        Ranking ranking = rankingService.create(
                chatroom,
                rankers,
                date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));

        if (LocalDate.now().equals(date)) {
            List<ChatParticipant> participants = chatParticipantService.findAllByChatroom(chatroom);
            ChatParticipant prevSaver = chatParticipantService.findByChatroomAndRankingStatus(
                    chatroom,
                    RankingStatus.SAVER);

            ChatParticipant prevFLexer = chatParticipantService.findByChatroomAndRankingStatus(
                    chatroom,
                    RankingStatus.FLEXER);

            chatParticipantService.updateAllRankingStatus(participants, RankingStatus.NONE);
            rankingService.updateRankingStatus(rankers);
            if (!Objects.isNull(prevSaver)) {
                eventPublisher.publishEvent(new UserProfileUpdateEvent(prevSaver.getUser().getUsername()));
            }
            if (!Objects.isNull(prevFLexer)) {
                eventPublisher.publishEvent(new UserProfileUpdateEvent(prevFLexer.getUser().getUsername()));
            }
        }

        return chatMessageService.saveRankingMessage(chatroom, ranking, date);
    }

    @Transactional
    public RankingResponsePayload calculateRanking(Chatroom chatroom) {
        Rankers rankers = rankingCalculator.calculate(chatroom);
        Ranking ranking = rankingService.create(chatroom, rankers);

        List<ChatParticipant> participants = chatParticipantService.findAllByChatroom(chatroom);

        ChatParticipant prevSaver = chatParticipantService.findByChatroomAndRankingStatus(
                chatroom,
                RankingStatus.SAVER);

        ChatParticipant prevFLexer = chatParticipantService.findByChatroomAndRankingStatus(
                chatroom,
                RankingStatus.FLEXER);

        chatParticipantService.updateAllRankingStatus(participants, RankingStatus.NONE);
        rankingService.updateRankingStatus(rankers);
        if (!Objects.isNull(prevSaver)) {
            eventPublisher.publishEvent(new UserProfileUpdateEvent(prevSaver.getUser().getUsername()));
        }
        if (!Objects.isNull(prevFLexer)) {
            eventPublisher.publishEvent(new UserProfileUpdateEvent(prevFLexer.getUser().getUsername()));
        }
        return chatMessageService.saveRankingMessage(chatroom, ranking);
    }

    @Transactional(readOnly = true)
    public LatestRankingResult getLatestRanking(String username, Long chatroomId) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        chatParticipantValidator.validateIsParticipate(user, chatroom);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonday = now
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate()
                .atStartOfDay();
        Ranking latestRanking = rankingService.findLatestRanking(chatroom, lastMonday, now);

        return getLatestRankingResponse(latestRanking, chatroom, lastMonday);
    }

    private LatestRankingResult getLatestRankingResponse(
            Ranking latestRanking,
            Chatroom chatroom,
            LocalDateTime lastMonday
    ) {
        if (latestRanking == null) {
            LatestRankingResponse response = RankingBuilder.buildNotFoundLatestRankingResponse(lastMonday);
            return LatestRankingResult.notFound(response);
        }

        ChatParticipant saver = chatParticipantService.findByUserIdAndChatroom(
                latestRanking.getSaverFirst(),
                chatroom
        );
        ChatParticipant flexer = chatParticipantService.findByUserIdAndChatroom(
                latestRanking.getFlexerFirst(),
                chatroom
        );

        LatestRankingResponse response = RankingBuilder.buildLatestRankingResponse(latestRanking, saver, flexer);
        return LatestRankingResult.found(response);
    }

    public record LatestRankingResult(boolean found, LatestRankingResponse response) {
        public static LatestRankingResult found(LatestRankingResponse response) {
            return new LatestRankingResult(true, response);
        }

        public static LatestRankingResult notFound(LatestRankingResponse response) {
            return new LatestRankingResult(false, response);
        }
    }
}
