package com.poortorich.ranking.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.response.ProfileResponse;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.chat.validator.ChatParticipantValidator;
import com.poortorich.global.date.util.DateParser;
import com.poortorich.ranking.entity.Ranking;
import com.poortorich.ranking.response.AllRankingsResponse;
import com.poortorich.ranking.response.LatestRankingResponse;
import com.poortorich.ranking.response.RankingInfoResponse;
import com.poortorich.ranking.service.RankingService;
import com.poortorich.ranking.util.RankingBuilder;
import com.poortorich.user.entity.User;
import com.poortorich.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RankingFacade {

    public record LatestRankingResult(boolean found, LatestRankingResponse response) {
        public static LatestRankingResult found(LatestRankingResponse response) {
            return new LatestRankingResult(true, response);
        }

        public static LatestRankingResult notFound(LatestRankingResponse response) {
            return new LatestRankingResult(false, response);
        }
    }

    private static final int PAGE_SIZE = 21;

    private final UserService userService;
    private final ChatroomService chatroomService;
    private final ChatParticipantService chatParticipantService;
    private final ChatParticipantValidator chatParticipantValidator;
    private final RankingService rankingService;

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

    @Transactional(readOnly = true)
    public AllRankingsResponse getAllRankings(String username, Long chatroomId, String cursor) {
        User user = userService.findUserByUsername(username);
        Chatroom chatroom = chatroomService.findById(chatroomId);
        chatParticipantValidator.validateIsParticipate(user, chatroom);

        Map<LocalDateTime, Ranking> rankings = getMondayRankings(chatroom, cursor);

        boolean hasNext = rankings.size() == PAGE_SIZE;
        LocalDateTime lastKey = rankings.keySet()
                .stream()
                .reduce((first, second) -> second)
                .orElse(null);

        return buildAllRankingsResponse(
                hasNext,
                hasNext ? lastKey.toLocalDate().toString() : null,
                rankings
        );
    }

    private Map<LocalDateTime, Ranking> getMondayRankings(Chatroom chatroom, String cursor) {
        List<LocalDateTime> mondays = getMondays(DateParser.parseDate(cursor).atStartOfDay(), getFloorMonday(chatroom));
        List<Ranking> rankings = rankingService.findAllRankings(chatroom, mondays);

        Map<LocalDateTime, Ranking> byDate = rankings.stream()
                .collect(Collectors.toMap(Ranking::getCreatedDate, ranking -> ranking));

        Map<LocalDateTime, Ranking> result = new LinkedHashMap<>();
        for (LocalDateTime monday : mondays) {
            result.put(monday, byDate.getOrDefault(monday, null));
        }
        return result;
    }

    private LocalDateTime getFloorMonday(Chatroom chatroom) {
        return chatroom.getCreatedDate()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
                .toLocalDate()
                .atStartOfDay();
    }

    private List<LocalDateTime> getMondays(LocalDateTime cursor, LocalDateTime floorMonday) {
        LocalDateTime recentMonday = cursor
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate()
                .atStartOfDay();

        List<LocalDateTime> mondays = IntStream.range(0, PAGE_SIZE)
                .mapToObj(recentMonday::minusWeeks)
                .toList();

        return mondays.stream()
                .filter(m -> !m.isBefore(floorMonday))
                .toList();
    }

    private AllRankingsResponse buildAllRankingsResponse(
            Boolean hasNext,
            String nextCursor,
            Map<LocalDateTime, Ranking> rankings
    ) {
        return AllRankingsResponse.builder()
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .rankings(rankings.entrySet().stream()
                        .limit(rankings.size() - 1)
                        .map(entry -> buildRankingInfoResponse(entry.getKey(), entry.getValue()))
                        .toList())
                .build();
    }

    private RankingInfoResponse buildRankingInfoResponse(LocalDateTime rankingAt, Ranking ranking) {
        if (ranking == null) {
            return RankingInfoResponse.builder()
                    .rankingAt(rankingAt.toLocalDate().toString())
                    .saverRankings(List.of())
                    .flexerRankings(List.of())
                    .build();
        }

        return RankingInfoResponse.builder()
                .rankingId(ranking.getId())
                .rankingAt(rankingAt.toLocalDate().toString())
                .saverRankings(buildProfileResponse(
                        Arrays.asList(ranking.getSaverFirst(), ranking.getSaverSecond(), ranking.getSaverThird()),
                        RankingStatus.SAVER)
                )
                .flexerRankings(buildProfileResponse(
                        Arrays.asList(ranking.getFlexerFirst(), ranking.getFlexerSecond(), ranking.getFlexerThird()),
                        RankingStatus.FLEXER)
                )
                .build();
    }

    private List<ProfileResponse> buildProfileResponse(List<Long> chatParticipantIds, RankingStatus type) {
        List<ChatParticipant> participants = chatParticipantService.findAllByIdIn(chatParticipantIds);

        return IntStream.range(0, chatParticipantIds.size())
                .mapToObj(i -> {
                    ChatParticipant chatParticipant = participants.stream()
                            .filter(p -> Objects.equals(p.getId(), chatParticipantIds.get(i)))
                            .findFirst()
                            .orElse(null);

                    if (chatParticipant == null) {
                        return null;
                    }

                    return ChatBuilder.buildProfileResponse(chatParticipant, i == 0 ? type : RankingStatus.NONE);
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
