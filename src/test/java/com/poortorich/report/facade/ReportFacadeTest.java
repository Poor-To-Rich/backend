package com.poortorich.report.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.report.request.ReceiptReportRequest;
import com.poortorich.report.response.ReceiptReportResponse;
import com.poortorich.report.service.ReportService;
import com.poortorich.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportFacadeTest {

    @Mock
    private ChatParticipantService chatParticipantService;
    @Mock
    private ChatroomService chatroomService;
    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportFacade reportFacade;

    @Test
    @DisplayName("채팅방 참여자 신고 성공")
    void reportMemberSuccess() {
        String username = "test";
        Long chatroomId = 1L;
        Long reportedId = 2L;
        ReceiptReportRequest request = new ReceiptReportRequest("INSULT", null);

        User reporter = User.builder().username(username).build();
        User reported = User.builder().id(reportedId).build();
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();
        ChatParticipant reporterMember = ChatParticipant.builder()
                .user(reporter)
                .chatroom(chatroom)
                .build();
        ChatParticipant reportedMember = ChatParticipant.builder()
                .user(reported)
                .chatroom(chatroom)
                .build();

        when(chatroomService.findById(chatroomId)).thenReturn(chatroom);
        when(chatParticipantService.findByUsernameAndChatroom(username, chatroom)).thenReturn(reporterMember);
        when(chatParticipantService.findByUserIdAndChatroom(reportedId, chatroom)).thenReturn(reportedMember);

        ReceiptReportResponse result = reportFacade.reportMember(username, chatroomId, reportedId, request);

        verify(reportService).reportMember(reporterMember, reportedMember, chatroom, request);

        assertThat(result.getReportType()).isEqualTo(request.getReportType());
        assertThat(result.getReportedUserId()).isEqualTo(reportedId);
    }
}