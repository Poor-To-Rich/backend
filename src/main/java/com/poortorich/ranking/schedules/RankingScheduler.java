package com.poortorich.ranking.schedules;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.ranking.facade.RankingFacade;
import com.poortorich.ranking.payload.response.RankingResponsePayload;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RankingScheduler {

    private final RankingFacade rankingFacade;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatroomService chatroomService;
    private final TaskExecutor taskExecutor;

    public RankingScheduler(
            RankingFacade rankingFacade,
            SimpMessagingTemplate messagingTemplate,
            ChatroomService chatroomService,
            @Qualifier("rankingTaskExecutor") TaskExecutor taskExecutor
    ) {
        this.rankingFacade = rankingFacade;
        this.messagingTemplate = messagingTemplate;
        this.chatroomService = chatroomService;
        this.taskExecutor = taskExecutor;
    }
    
    @Scheduled(cron = "0 0 12 * * MON")
    public void calculateAndBroadcastWeeklyRanking() {
        List<Chatroom> activeChatrooms = chatroomService.getChatroomsByRankingEnabledIsTrue();

        AtomicInteger counter = new AtomicInteger();
        Collection<List<Chatroom>> batches = activeChatrooms.stream()
                .collect(Collectors.groupingBy(chatroom -> counter.getAndIncrement() / 100))
                .values();

        batches.forEach(batch -> {
            CompletableFuture.runAsync(() -> {
                try {
                    processBatch(batch);
                } catch (Exception exception) {
                    log.error("랭킹 집계 실패");
                }
            }, taskExecutor);
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
