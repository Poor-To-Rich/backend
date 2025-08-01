package com.poortorich.chat.controller;

import com.poortorich.chat.facade.ChatFacade;
import com.poortorich.chat.realtime.facade.ChatRealTimeFacade;
import com.poortorich.chat.request.ChatroomCreateRequest;
import com.poortorich.chat.response.ChatroomCreateResponse;
import com.poortorich.chat.response.ChatroomInfoResponse;
import com.poortorich.chat.response.enums.ChatResponse;
import com.poortorich.global.config.BaseSecurityTest;
import com.poortorich.s3.util.S3TestFileGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@ExtendWith(MockitoExtension.class)
public class ChatControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatFacade chatFacade;
    @MockitoBean
    private SimpMessagingTemplate simpMessagingTemplate;
    @MockitoBean
    private ChatRealTimeFacade chatRealTimeFacade;

    @Test
    @WithMockUser(username = "test")
    @DisplayName("채팅방 추가 성공")
    void createChatroomSuccess() throws Exception {
        MockMultipartFile chatroomImage = S3TestFileGenerator.createJpegFile();
        when(chatFacade.createChatroom(eq("test"), any(ChatroomCreateRequest.class)))
                .thenReturn(ChatroomCreateResponse.builder().build());

        mockMvc.perform(multipart("/chatrooms")
                        .file(chatroomImage)
                        .param("chatroomTitle", "부자될거지")
                        .param("maxMemberCount", "10")
                        .param("description", "부자될거죠?")
                        .param("hashtags", "태그1", "태그2")
                        .param("isRankingEnabled", "false")
                        .param("chatroomPassword", "비밀번호123")
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(("$.message"))
                        .value(ChatResponse.CREATE_CHATROOM_SUCCESS.getMessage())
                );
    }

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
