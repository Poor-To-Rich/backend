package com.poortorich.chatnotice.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.response.LatestNoticeResponse;
import com.poortorich.chatnotice.service.ChatNoticeService;
import com.poortorich.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatNoticeFacadeTest {

    @Mock
    private ChatroomService chatroomService;
    @Mock
    private ChatParticipantService chatParticipantService;
    @Mock
    private ChatNoticeService chatNoticeService;

    @InjectMocks
    private ChatNoticeFacade chatNoticeFacade;

    private final String username = "test";
    private final Long chatroomId = 1L;
    private User user;
    private Chatroom chatroom;
    private ChatParticipant chatParticipant;
    private ChatNotice chatNotice;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username(username)
                .nickname(username)
                .build();

        chatroom = Chatroom.builder()
                .id(chatroomId)
                .build();

        chatParticipant = ChatParticipant.builder()
                .user(user)
                .chatroom(chatroom)
                .noticeStatus(NoticeStatus.DEFAULT)
                .build();

        chatNotice = ChatNotice.builder()
                .id(1L)
                .chatroom(chatroom)
                .author(user)
                .content("공지 내용")
                .createdDate(LocalDateTime.of(2025, 8, 8, 0, 0))
                .build();
    }

    @Test
    @DisplayName("최근 공지 조회 성공")
    void getLatestNoticeSuccess() {
        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatParticipantService.findByUsernameAndChatroom(username, chatroom)).thenReturn(chatParticipant);
        when(chatNoticeService.getLatestNotice(chatroom)).thenReturn(chatNotice);

        LatestNoticeResponse result = chatNoticeFacade.getLatestNotice(username, chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.getNoticeId()).isEqualTo(chatNotice.getId());
        assertThat(result.getStatus()).isEqualTo(chatParticipant.getNoticeStatus().toString());
        assertThat(result.getPreview()).isEqualTo(chatNotice.getContent());
        assertThat(result.getAuthorNickname()).isEqualTo(user.getNickname());
        assertThat(result.getCreatedAt()).isEqualTo(chatNotice.getCreatedDate().toString());
    }

    @Test
    @DisplayName("공지 내용이 30자 초과인 경우 30자만 표출")
    void getLatestNoticePreviewTest() {
        String preview = "코딩하다가 정신 차려보니 새벽 3시가 되었다. 피곤하지";
        chatNotice = ChatNotice.builder()
                .id(1L)
                .chatroom(chatroom)
                .author(user)
                .content("코딩하다가 정신 차려보니 새벽 3시가 되었다. 피곤하지만 너무 재밌어서 멈출수가 없다.")
                .createdDate(LocalDateTime.of(2025, 8, 8, 0, 0))
                .build();

        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatParticipantService.findByUsernameAndChatroom(username, chatroom)).thenReturn(chatParticipant);
        when(chatNoticeService.getLatestNotice(chatroom)).thenReturn(chatNotice);

        LatestNoticeResponse result = chatNoticeFacade.getLatestNotice(username, chatroomId);

        assertThat(result).isNotNull();
        assertThat(result.getNoticeId()).isEqualTo(chatNotice.getId());
        assertThat(result.getStatus()).isEqualTo(chatParticipant.getNoticeStatus().toString());
        assertThat(result.getPreview()).isEqualTo(preview);
        assertThat(result.getAuthorNickname()).isEqualTo(user.getNickname());
        assertThat(result.getCreatedAt()).isEqualTo(chatNotice.getCreatedDate().toString());
    }
}
