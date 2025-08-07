package com.poortorich.like.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.poortorich.global.config.BaseSecurityTest;
import com.poortorich.like.constants.LikeResponseMessage;
import com.poortorich.like.facade.LikeFacade;
import com.poortorich.like.request.LikeUpdateRequest;
import com.poortorich.like.response.enums.LikeResponse;
import com.poortorich.like.response.LikeStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@ExtendWith(MockitoExtension.class)
class LikeControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LikeFacade likeFacade;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    @WithMockUser(username = "test")
    @DisplayName("채팅방 좋아요 상태 조회 성공")
    void getChatroomLikeSuccess() throws Exception {
        Long chatroomId = 1L;

        when(likeFacade.getChatroomLike(eq("test"), eq(chatroomId)))
                .thenReturn(LikeStatusResponse.builder().build());

        mockMvc.perform(get("/chatrooms/" + chatroomId + "/like")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(LikeResponse.GET_LIKE_STATUS_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = "test")
    @DisplayName("채팅방 좋아요 상태 변경 성공")
    void updateChatroomLikeSuccess() throws Exception {
        Long chatroomId = 1L;
        LikeUpdateRequest request = new LikeUpdateRequest(true);

        when(likeFacade.updateChatroomLike(eq("test"), eq(chatroomId), eq(request)))
                .thenReturn(LikeStatusResponse.builder().build());

        mockMvc.perform(patch("/chatrooms/" + chatroomId + "/like")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(LikeResponse.UPDATE_LIKE_STATUS_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = "test")
    @DisplayName("채팅방 좋아요 상태 입력값이 null인 경우 예외 발생")
    void updateChatroomLikeRequestNull() throws Exception {
        Long chatroomId = 1L;
        LikeUpdateRequest request = new LikeUpdateRequest(null);

        when(likeFacade.updateChatroomLike(eq("test"), eq(chatroomId), eq(request)))
                .thenReturn(LikeStatusResponse.builder().build());

        mockMvc.perform(patch("/chatrooms/" + chatroomId + "/like")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(LikeResponseMessage.IS_LIKED_REQUIRED)
                );
    }
}
