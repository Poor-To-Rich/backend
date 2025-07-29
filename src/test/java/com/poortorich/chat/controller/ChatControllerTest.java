package com.poortorich.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chat.response.ChatroomInfoResponse;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.config.BaseSecurityTest;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(ChatController.class)
@ExtendWith(MockitoExtension.class)
public class ChatControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatFacade chatFacade;

    @Test
    @WithMockUser(username = "test")
    @DisplayName("채팅방 정보 조회 성공")
    void getChatroomSuccess() throws Exception {
        Long chatroomId = 1L;
        when(chatFacade.getChatroom(chatroomId)).thenReturn(ChatroomInfoResponse.builder().build());

        mockMvc.perform(get("/chatrooms/" + chatroomId + "/edit")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(ChatResponse.GET_CHATROOM_SUCCESS.getMessage())
                );
    }
}
