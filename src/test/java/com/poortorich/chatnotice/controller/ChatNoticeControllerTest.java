package com.poortorich.chatnotice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chatnotice.constants.ChatNoticeResponseMessage;
import com.poortorich.chatnotice.request.ChatNoticeUpdateRequest;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.global.config.BaseSecurityTest;
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
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatNoticeController.class)
@ExtendWith(MockitoExtension.class)
class ChatNoticeControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatFacade chatFacade;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    @WithMockUser(username = "test")
    @DisplayName("공지 상태 변경 성공")
    void updateNoticeStatusSuccess() throws Exception {
        long chatroomId = 1L;
        ChatNoticeUpdateRequest request = new ChatNoticeUpdateRequest("TEMP_HIDDEN");

        mockMvc.perform(patch("/chatrooms/" + chatroomId + "/notices")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(ChatNoticeResponse.UPDATE_CHAT_NOTICE_STATUS_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = "test")
    @DisplayName("공지 상태 변경 입력값이 없는 경우 예외 발생")
    void updateNoticeStatusRequestNull() throws Exception {
        long chatroomId = 1L;
        ChatNoticeUpdateRequest request = new ChatNoticeUpdateRequest(null);

        mockMvc.perform(patch("/chatrooms/" + chatroomId + "/notices")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ChatNoticeResponseMessage.NOTICE_STATUS_REQUIRED)
                );
    }
}