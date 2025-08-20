package com.poortorich.chat.controller;

import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chat.response.AllParticipantsResponse;
import com.poortorich.chat.response.ChatroomsResponse;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.config.BaseSecurityTest;
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

@WebMvcTest(ChatParticipantController.class)
@ExtendWith(MockitoExtension.class)
class ChatParticipantControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatFacade chatFacade;

    private final String username = "test";

    @Test
    @WithMockUser(username = username)
    @DisplayName("내가 방장인 채팅방 조회 성공")
    void getHostedChatroomsSuccess() throws Exception {
        ChatroomsResponse response = ChatroomsResponse.builder().build();
        when(chatFacade.getHostedChatrooms(username)).thenReturn(response);

        mockMvc.perform(get("/users/hosted-chatrooms")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.message"))
                        .value(ChatResponse.GET_HOSTED_CHATROOMS_SUCCESS.getMessage()));
    }

    @Test
    @WithMockUser(username = username)
    @DisplayName("전체 참여 인원 조회 성공")
    void getAllParticipantsSuccess() throws Exception {
        Long chatroomId = 1L;

        when(chatFacade.getAllParticipants(chatroomId)).thenReturn(AllParticipantsResponse.builder().build());

        mockMvc.perform(get("/chatrooms/" + chatroomId + "/members/all")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.message")).value(ChatResponse.GET_ALL_PARTICIPANTS_SUCCESS.getMessage()));
    }
}
