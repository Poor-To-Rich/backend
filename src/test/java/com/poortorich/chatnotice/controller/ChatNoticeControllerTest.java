package com.poortorich.chatnotice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chatnotice.constants.ChatNoticeResponseMessage;
import com.poortorich.chatnotice.facade.ChatNoticeFacade;
import com.poortorich.chatnotice.request.ChatNoticeUpdateRequest;
import com.poortorich.chatnotice.response.LatestNoticeResponse;
import com.poortorich.chatnotice.response.PreviewNoticesResponse;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.global.config.BaseSecurityTest;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatNoticeController.class)
@ExtendWith(MockitoExtension.class)
class ChatNoticeControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatFacade chatFacade;
    @MockitoBean
    private ChatNoticeFacade chatNoticeFacade;

    private final String username = "test";

    @Test
    @WithMockUser(username = username)
    @DisplayName("최신 공지 조회 성공")
    void getLatestNoticeSuccess() throws Exception {
        Long chatroomId = 1L;

        when(chatNoticeFacade.getLatestNotice(username, chatroomId)).thenReturn(LatestNoticeResponse.builder().build());

        mockMvc.perform(get("/chatrooms/" + chatroomId + "/notices")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(ChatNoticeResponse.GET_LATEST_NOTICE_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = username)
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
                        .value(ChatNoticeResponse.UPDATE_NOTICE_STATUS_SUCCESS.getMessage())
                );
    }

    @Test
    @WithMockUser(username = username)
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

    @Test
    @WithMockUser(username = username)
    @DisplayName("최근 공지 목록 조회 성공")
    void getPreviewNoticesSuccess() throws Exception {
        Long chatroomId = 1L;

        when(chatNoticeFacade.getPreviewNotices(chatroomId)).thenReturn(PreviewNoticesResponse.builder().build());

        mockMvc.perform(get("/chatrooms/" + chatroomId + "/notices/preview")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(ChatNoticeResponse.GET_PREVIEW_NOTICE_SUCCESS.getMessage()));
    }
}
