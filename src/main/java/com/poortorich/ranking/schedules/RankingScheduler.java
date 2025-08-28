package com.poortorich.ranking.schedules;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.ranking.facade.RankingFacade;
import com.poortorich.ranking.payload.response.RankingResponsePayload;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RankingScheduler {

    private final RankingFacade rankingFacade;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatroomService chatroomService;

    @Scheduled(cron = "0 0 12 * * MON")
    public void calculateAndBroadcastWeeklyRanking() {
        List<Chatroom> activeChatrooms = chatroomService.getChatroomsByRankingEnabledIsTrue();

        AtomicInteger counter = new AtomicInteger();
        Collection<List<Chatroom>> batches = activeChatrooms.stream()
                .collect(Collectors.groupingBy(chatroom -> counter.getAndIncrement() / 100))
                .values();

        batches.forEach(batch -> {
            CompletableFuture.runAsync(() -> processBatch(batch));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void processBatch(List<Chatroom> chatrooms) {
        chatrooms.forEach(chatroom -> {
            RankingResponsePayload payload = rankingFacade.calculateRanking(chatroom);

            if (!Objects.isNull(payload)) {
                messagingTemplate.convertAndSend(
                        SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroom.getId(),
                        payload.mapToBasePayload()
                );
            }
        });
    }
}
