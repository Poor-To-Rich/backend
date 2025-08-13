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
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
        ChatParticipant reporter = ChatParticipant.builder().chatroom(chatroom).build();
        ChatParticipant reported = ChatParticipant.builder().chatroom(chatroom).build();

        ReceiptReportRequest request = new ReceiptReportRequest("INSULT", null);

        when(reportRepository.existsByReporterAndReportedAndChatroomAndReason(
                reporter,
                reported,
                chatroom,
                ReportReason.INSULT)
        ).thenReturn(false);

        reportService.reportMember(reporter, reported, chatroom, request);

        verify(reportRepository).save(reportCaptor.capture());
        Report report = reportCaptor.getValue();

        assertThat(report.getReporter()).isEqualTo(reporter);
        assertThat(report.getReported()).isEqualTo(reported);
        assertThat(report.getChatroom()).isEqualTo(chatroom);
        assertThat(report.getReason()).isEqualTo(request.parseReportReason());
    }

    @Test
    @DisplayName("이미 같은 조합으로 신고를 진행한 경우 예외 발생")
    void reportMemberConflict() {
        Chatroom chatroom = Chatroom.builder().build();
        ChatParticipant reporter = ChatParticipant.builder().chatroom(chatroom).build();
        ChatParticipant reported = ChatParticipant.builder().chatroom(chatroom).build();

        ReceiptReportRequest request = new ReceiptReportRequest("INSULT", null);

        when(reportRepository.existsByReporterAndReportedAndChatroomAndReason(
                reporter,
                reported,
                chatroom,
                ReportReason.INSULT)
        ).thenReturn(true);

        assertThatThrownBy(() -> reportService.reportMember(reporter, reported, chatroom, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(ReportResponse.REPORT_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("사용자 신고 동시성 상황: 유니크 제약 위반 시 ConflictException 변환")
    void reportMemberDuplicateOnSave() {
        Chatroom chatroom = Chatroom.builder().build();
        ChatParticipant reporter = ChatParticipant.builder().chatroom(chatroom).build();
        ChatParticipant reported = ChatParticipant.builder().chatroom(chatroom).build();
        ReceiptReportRequest request = new ReceiptReportRequest("INSULT", null);

        when(reportRepository.existsByReporterAndReportedAndChatroomAndReason(
                reporter,
                reported,
                chatroom,
                ReportReason.INSULT)
        ).thenReturn(false);

        when(reportRepository.save(any(Report.class)))
                .thenThrow(new DataIntegrityViolationException("unique constraint"));

        assertThatThrownBy(() -> reportService.reportMember(reporter, reported, chatroom, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(ReportResponse.REPORT_DUPLICATE.getMessage());
    }
}
