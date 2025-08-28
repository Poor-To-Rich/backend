package com.poortorich.ranking.util.calculator;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.service.AccountBookService;
import com.poortorich.accountbook.util.AccountBookCalculator;
import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.ranking.model.Rankers;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RankingCalculator {

    private final ChatParticipantService participantService;
    private final AccountBookService accountBookService;

    public Rankers calculate(Chatroom chatroom) {
        List<ChatParticipant> participants = participantService.findAllByChatroom(chatroom);
        RankingCalculationData rankingCalculationData = getRankingCalculationData(participants);

        if (!rankingCalculationData.isCalculate()) {
            return null;
        }

        return Rankers.builder()
                .savers(calculateSaver(rankingCalculationData))
                .flexers(calculateFlexer(rankingCalculationData))
                .build();
    }

    private RankingCalculationData getRankingCalculationData(List<ChatParticipant> participants) {
        LocalDate today = LocalDate.now();
        LocalDate lastWeekMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .minusWeeks(1);

        List<CalculableParticipant> calculableParticipants = new ArrayList<>();

        for (ChatParticipant participant : participants) {
            int expenseDaysCount = 0;

            for (int i = 0; i < 7; i++) {
                LocalDate checkDate = lastWeekMonday.plusDays(i);
                List<AccountBook> expenses = accountBookService.getAccountBooksByUserAndDate(
                        participant.getUser(),
                        checkDate,
                        AccountBookType.EXPENSE);

                if (!expenses.isEmpty()) {
                    expenseDaysCount++;
                }
            }

            if (expenseDaysCount >= 3) {
                calculableParticipants.add(CalculableParticipant.builder()
                        .expenseDays(expenseDaysCount)
                        .participant(participant)
                        .build());
            }
        }

        return RankingCalculationData.builder()
                .startDate(lastWeekMonday)
                .endDate(lastWeekMonday.plusDays(6))
                .participants(calculableParticipants)
                .build();
    }

    private List<ChatParticipant> calculateSaver(RankingCalculationData calculationData) {
        List<CalculableParticipant> participants = calculationData.getParticipants();
        List<RankingResult> rankingResults = participants.stream()
                .map(participant -> RankingResult.builder()
                        .score(getScore(participant, calculationData.getStartDate(), calculationData.getEndDate()))
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
                        .score(getScore(participant, calculationData.getStartDate(), calculationData.getEndDate()))
                        .participant(participant.getParticipant())
                        .build())
                .sorted(Comparator.reverseOrder())
                .toList();

        return rankingResults.stream()
                .map(RankingResult::getParticipant)
                .toList();
    }

    private Double getScore(CalculableParticipant participant, LocalDate startDate, LocalDate endDate) {
        List<AccountBook> accountBooks = accountBookService.getAccountBookBetweenDates(
                participant.getParticipant().getUser(),
                startDate,
                endDate,
                AccountBookType.EXPENSE
        );

        Long totalSpending = AccountBookCalculator.sum(accountBooks);

        return totalSpending * (participant.getExpenseDays() / 7.);
    }

    @Getter
    @Builder
    protected static class CalculableParticipant {
        private int expenseDays;
        private ChatParticipant participant;
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
        private Double score;
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
