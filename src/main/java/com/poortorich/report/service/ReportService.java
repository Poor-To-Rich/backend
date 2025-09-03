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
import org.springframework.transaction.annotation.Transactional;

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
        ReportReason reason = request.parseReportReason();
        validateReport(reporterMember, reportedMember, chatroom, reason);

        try {
            reportRepository.save(
                    ReportBuilder.buildReport(
                            reporterMember,
                            reportedMember,
                            chatroom,
                            reason,
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

    @Transactional
    public void deleteAllByChatroom(Chatroom chatroom) {
        reportRepository.deleteByChatroom(chatroom);
    }
}
