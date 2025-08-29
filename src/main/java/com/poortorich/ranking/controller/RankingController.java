package com.poortorich.ranking.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.ranking.facade.RankingFacade;
import com.poortorich.ranking.response.enums.RankingResponse;
import com.poortorich.websocket.stomp.command.subscribe.endpoint.SubscribeEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping("/chatrooms/{chatroomId}/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingFacade rankingFacade;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/preview")
    public ResponseEntity<BaseResponse> getLatestRanking(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId
    ) {
        var result = rankingFacade.getLatestRanking(userDetails.getUsername(), chatroomId);
        RankingResponse code = result.found()
                ? RankingResponse.GET_LATEST_RANKING_SUCCESS
                : RankingResponse.GET_LATEST_RANKING_NOT_FOUND;
        return DataResponse.toResponseEntity(code, result.response());
    }

    @PostMapping("/test/calculate")
    public ResponseEntity<BaseResponse> calculateRankingTest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatroomId,
            @RequestParam(name = "date", required = false) LocalDate date
    ) {
        if (Objects.isNull(date)) {
            date = LocalDate.now();
        }
        var payload = rankingFacade.calculateRankingTest(chatroomId, date);
        messagingTemplate.convertAndSend(
                SubscribeEndpoint.CHATROOM_SUBSCRIBE_PREFIX + chatroomId,
                payload.mapToBasePayload());
        return DataResponse.toResponseEntity(
                HttpStatus.OK,
                "랭킹 집계 테스트 성공",
                payload.mapToBasePayload());
    }
}
