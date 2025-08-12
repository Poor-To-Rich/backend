package com.poortorich.report.service;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.global.exceptions.ConflictException;
import com.poortorich.report.entity.enums.ReportReason;
import com.poortorich.report.repository.ReportRepository;
import com.poortorich.report.request.ReceiptReportRequest;
import com.poortorich.report.response.enums.ReportResponse;
import com.poortorich.report.util.ReportBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public void reportMember(
            ChatParticipant reporterMember,
            ChatParticipant reportedMember,
            Chatroom chatroom,
            ReceiptReportRequest request
    ) {
        // TODO : 같은 조합으로도 신고가 가능하게 할 지, 아니면 신고에 개수 제한을 둘 지
        if (reportRepository.existsByReporterAndReportedAndChatroom(reporterMember, reportedMember, chatroom)) {
            throw new ConflictException(ReportResponse.REPORT_DUPLICATE);
        }

        reportRepository.save(
                ReportBuilder.buildReport(
                        reporterMember,
                        reportedMember,
                        chatroom,
                        ReportReason.valueOf(request.getReportType()),
                        request.getCustomReason())
        );
    }
}
