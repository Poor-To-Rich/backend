package com.poortorich.chat.controller;

import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chat.response.AllParticipantsResponse;
import com.poortorich.chat.response.ChatroomsResponse;
import com.poortorich.chat.response.SearchParticipantsResponse;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.config.BaseSecurityTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(ChatParticipantController.class)
class ChatParticipantControllerTest extends BaseSecurityTest {

    private static final String USERNAME = "test";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatFacade chatFacade;

    @Test
    @WithMockUser(username = USERNAME)
    @DisplayName("내가 방장인 채팅방 조회 성공")
    void getHostedChatroomsSuccess() throws Exception {
        ChatroomsResponse response = ChatroomsResponse.builder().build();
        when(chatFacade.getHostedChatrooms(USERNAME)).thenReturn(response);

        mockMvc.perform(get("/users/hosted-chatrooms")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.message"))
                        .value(ChatResponse.GET_HOSTED_CHATROOMS_SUCCESS.getMessage()));
    }

    @Test
    @WithMockUser(username = USERNAME)
    @DisplayName("전체 참여 인원 조회 성공")
    void getAllParticipantsSuccess() throws Exception {
        Long chatroomId = 1L;

        when(chatFacade.getAllParticipants(USERNAME, chatroomId)).thenReturn(AllParticipantsResponse.builder().build());

        mockMvc.perform(get("/chatrooms/" + chatroomId + "/members/all")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.message")).value(ChatResponse.GET_ALL_PARTICIPANTS_SUCCESS.getMessage()));
    }

    @Test
    @WithMockUser(username = USERNAME)
    @DisplayName("참여 인원 검색 성공")
    void searchParticipantsSuccess() throws Exception {
        Long chatroomId = 1L;
        String nickname = "test";

        when(chatFacade.searchParticipantsByNickname(USERNAME, chatroomId, nickname))
                .thenReturn(SearchParticipantsResponse.builder().build());

        mockMvc.perform(get("/chatrooms/" + chatroomId + "/members/search")
                        .param("nickname", nickname)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.message")).value(ChatResponse.SEARCH_PARTICIPANTS_SUCCESS.getMessage()));
    }
}
