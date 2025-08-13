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
import org.springframework.dao.DataIntegrityViolationException;
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
        validateReport(reporterMember, reportedMember, chatroom, request.parseReportReason());

        try {
            reportRepository.save(
                    ReportBuilder.buildReport(
                            reporterMember,
                            reportedMember,
                            chatroom,
                            request.parseReportReason(),
                            request.getCustomReason())
            );
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(ReportResponse.REPORT_DUPLICATE);
        }
    }

    private void validateReport(
            ChatParticipant reporterMember,
            ChatParticipant reportedMember,
            Chatroom chatroom,
            ReportReason reason
    ) {
        if (reportRepository.existsByReporterAndReportedAndChatroomAndReason(
                reporterMember,
                reportedMember,
                chatroom,
                reason)
        ) {
            throw new ConflictException(ReportResponse.REPORT_DUPLICATE);
        }
    }
}
