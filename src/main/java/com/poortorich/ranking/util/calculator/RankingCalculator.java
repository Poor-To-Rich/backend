package com.poortorich.ranking.util.calculator;

import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.ranking.model.Rankers;
import com.poortorich.ranking.model.UserExpenseAggregate;
import com.poortorich.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RankingCalculator {

    private final ChatParticipantService participantService;
    private final AccountBookService accountBookService;

    public Rankers calculate(Chatroom chatroom) {
        List<ChatParticipant> participants = participantService.findAllByChatroom(chatroom);
        // TODO: null 파라미터 삭제
        RankingCalculationData rankingCalculationData = getRankingCalculationData(participants, null);

        if (!rankingCalculationData.isCalculate()) {
            return null;
        }

        return Rankers.builder()
                .savers(calculateSaver(rankingCalculationData))
                .flexers(calculateFlexer(rankingCalculationData))
                .build();
    }

    // TODO: 테스트 이후 삭제
    public Rankers calculate(Chatroom chatroom, LocalDate date) {
        List<ChatParticipant> participants = participantService.findAllByChatroom(chatroom);
        RankingCalculationData rankingCalculationData = getRankingCalculationData(participants, date);

        if (!rankingCalculationData.isCalculate()) {
            return null;
        }

        return Rankers.builder()
                .savers(calculateSaver(rankingCalculationData))
                .flexers(calculateFlexer(rankingCalculationData))
                .build();
    }

    // TODO: 테스트 이후 date 파라미터 삭제
    private RankingCalculationData getRankingCalculationData(List<ChatParticipant> participants, LocalDate date) {
        LocalDate today = (Objects.nonNull(date)) ? date : LocalDate.now();
        LocalDate lastWeekSunday = today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        LocalDate lastWeekMonday = lastWeekSunday.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));

        List<User> users = participants.stream().map(ChatParticipant::getUser).toList();

        List<UserExpenseAggregate> expenseAggregates = accountBookService
                .getExpenseAggregatesForUsersInRange(users, lastWeekMonday, lastWeekSunday);

        Map<Long, UserExpenseAggregate> aggregateGroupByUserId = expenseAggregates.stream()
                .collect(Collectors.toMap(UserExpenseAggregate::getUserId, aggregate -> aggregate));

        List<CalculableParticipant> calculableParticipants = participants.stream()
                .filter(participant -> {
                    UserExpenseAggregate aggregate = aggregateGroupByUserId.get(participant.getUser().getId());
                    return aggregate != null && aggregate.getExpenseDaysCount() >= 3;
                })
                .map(participant -> {
                    UserExpenseAggregate aggregate = aggregateGroupByUserId.get(participant.getUser().getId());
                    return CalculableParticipant.builder()
                            .expenseDays(aggregate.getExpenseDaysCount())
                            .participant(participant)
                            .totalExpenseCost(aggregate.getTotalCostAsBigDecimal())
                            .build();
                })
                .toList();

        return RankingCalculationData.builder()
                .startDate(lastWeekMonday)
                .endDate(lastWeekSunday)
                .participants(calculableParticipants)
                .build();
    }

    private List<ChatParticipant> calculateSaver(RankingCalculationData calculationData) {
        List<CalculableParticipant> participants = calculationData.getParticipants();
        List<RankingResult> rankingResults = participants.stream()
                .map(participant -> RankingResult.builder()
                        .score(getScore(participant))
                        .participant(participant.getParticipant())
                        .build())
                .sorted()
                .toList();

        return rankingResults.stream()
                .map(RankingResult::getParticipant)
                .toList();
    }

    private List<ChatParticipant> calculateFlexer(RankingCalculationData calculationData) {
        List<CalculableParticipant> participants = calculationData.getParticipants();
        List<RankingResult> rankingResults = participants.stream()
                .map(participant -> RankingResult.builder()
                        .score(getScore(participant))
                        .participant(participant.getParticipant())
                        .build())
                .sorted(Comparator.reverseOrder())
                .toList();

        return rankingResults.stream()
                .map(RankingResult::getParticipant)
                .toList();
    }

    private BigDecimal getScore(CalculableParticipant participant) {
        BigDecimal totalCost = participant.getTotalExpenseCost();
        double dayRatio = participant.getExpenseDays() / 7.;
        return totalCost.multiply(BigDecimal.valueOf(dayRatio));
    }

    @Getter
    @Builder
    protected static class CalculableParticipant {
        private int expenseDays;
        private ChatParticipant participant;
        private BigDecimal totalExpenseCost;
    }

    @Getter
    @Builder
    protected static class RankingCalculationData {
        private LocalDate startDate;
        private LocalDate endDate;
        private List<CalculableParticipant> participants;

        public boolean isCalculate() {
            return !participants.isEmpty() && participants.size() >= 2;
        }
    }

    @Getter
    @Builder
    protected static class RankingResult implements Comparable<RankingResult> {
        private BigDecimal score;
        private ChatParticipant participant;

        @Override
        public int compareTo(RankingResult other) {
            if (this.score == null && other.score == null) {
                return 0;
            }
            if (this.score == null) {
                return 1;
            }
            if (other.score == null) {
                return -1;
            }

            return this.score.compareTo(other.score);
        }
    }
}
