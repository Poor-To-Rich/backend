package com.poortorich.ranking.controller;

import com.poortorich.global.config.BaseSecurityTest;
import com.poortorich.ranking.facade.RankingFacade;
import com.poortorich.ranking.response.LatestRankingResponse;
import com.poortorich.ranking.response.enums.RankingResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RankingController.class)
@ExtendWith(MockitoExtension.class)
class RankingControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RankingFacade rankingFacade;

    private final String username = "test";

    @Test
    @WithMockUser(username = username)
    @DisplayName("최신 랭킹 조회 성공")
    void getLatestRankingSuccess() throws Exception {
        Long chatroomId = 1L;

        when(rankingFacade.getLatestRanking(username, chatroomId)).thenReturn(
                RankingFacade.LatestRankingResult.create(true, LatestRankingResponse.builder().build())
        );

        mockMvc.perform(get("/chatrooms/" + chatroomId + "/rankings")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(RankingResponse.GET_LATEST_RANKING_SUCCESS.getMessage()));
    }

    @Test
    @WithMockUser(username = username)
    @DisplayName("최신 랭킹이 없는 경우 null 조회 성공")
    void getLatestRankingNull() throws Exception {
        Long chatroomId = 1L;

        when(rankingFacade.getLatestRanking(username, chatroomId)).thenReturn(
                RankingFacade.LatestRankingResult.create(false, LatestRankingResponse.builder().build())
        );

        mockMvc.perform(get("/chatrooms/" + chatroomId + "/rankings")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(RankingResponse.GET_LATEST_RANKING_NOT_FOUND.getMessage()));
    }
}
