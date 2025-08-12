package com.poortorich.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.global.config.BaseSecurityTest;
import com.poortorich.report.facade.ReportFacade;
import com.poortorich.report.request.ReceiptReportRequest;
import com.poortorich.report.response.ReceiptReportResponse;
import com.poortorich.report.response.enums.ReportResponse;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@ExtendWith(MockitoExtension.class)
class ReportControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportFacade reportFacade;

    private final String username = "test";
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(username = username)
    @DisplayName("채팅방 참여자 신고 성공")
    void reportMemberSuccess() throws Exception {
        Long chatroomId = 1L;
        Long userId = 1L;
        ReceiptReportRequest request = new ReceiptReportRequest("INSULT", null);

        when(reportFacade.reportMember(username, chatroomId, userId, request))
                .thenReturn(ReceiptReportResponse.builder().build());

        mockMvc.perform(post("/chatrooms/" + chatroomId + "/members/" + userId + "/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.message")).value(ReportResponse.MEMBER_REPORT_SUCCESS.getMessage()));
    }
}
