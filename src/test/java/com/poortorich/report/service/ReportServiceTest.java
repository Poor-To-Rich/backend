package com.poortorich.report.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.global.exceptions.ConflictException;
import com.poortorich.report.entity.Report;
import com.poortorich.report.entity.enums.ReportReason;
import com.poortorich.report.repository.ReportRepository;
import com.poortorich.report.request.ReceiptReportRequest;
import com.poortorich.report.response.enums.ReportResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    @Captor
    private ArgumentCaptor<Report> reportCaptor;

    @Test
    @DisplayName("채팅방 참여자 신고 성공")
    void reportMemberSuccess() {
        Chatroom chatroom = Chatroom.builder().build();
        ChatParticipant reporterMember = ChatParticipant.builder().chatroom(chatroom).build();
        ChatParticipant reportedMember = ChatParticipant.builder().chatroom(chatroom).build();

        ReceiptReportRequest request = new ReceiptReportRequest("INSULT", null);

        when(reportRepository.existsByReporterAndReportedAndChatroom(reporterMember, reportedMember, chatroom))
                .thenReturn(false);

        reportService.reportMember(reporterMember, reportedMember, chatroom, request);

        verify(reportRepository).save(reportCaptor.capture());
        Report report = reportCaptor.getValue();

        assertThat(report.getReporter()).isEqualTo(reporterMember);
        assertThat(report.getReported()).isEqualTo(reportedMember);
        assertThat(report.getChatroom()).isEqualTo(chatroom);
        assertThat(report.getReason()).isEqualTo(ReportReason.valueOf(request.getReportType()));
    }

    @Test
    @DisplayName("이미 같은 조합으로 신고를 진행한 경우 예외 발생")
    void reportMemberConflict() {
        Chatroom chatroom = Chatroom.builder().build();
        ChatParticipant reporterMember = ChatParticipant.builder().chatroom(chatroom).build();
        ChatParticipant reportedMember = ChatParticipant.builder().chatroom(chatroom).build();

        ReceiptReportRequest request = new ReceiptReportRequest("INSULT", null);

        when(reportRepository.existsByReporterAndReportedAndChatroom(reporterMember, reportedMember, chatroom))
                .thenReturn(true);

        assertThatThrownBy(() -> reportService.reportMember(reporterMember, reportedMember, chatroom, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(ReportResponse.REPORT_DUPLICATE.getMessage());
    }
}
