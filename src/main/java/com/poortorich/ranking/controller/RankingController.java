package com.poortorich.ranking.controller;

import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.ranking.facade.RankingFacade;
import com.poortorich.ranking.response.enums.RankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatrooms/{chatroomId}/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingFacade rankingFacade;

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
}
