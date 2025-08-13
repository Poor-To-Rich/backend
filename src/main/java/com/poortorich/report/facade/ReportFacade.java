package com.poortorich.report.facade;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.service.ChatParticipantService;
import com.poortorich.chat.service.ChatroomService;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.report.request.ReceiptReportRequest;
import com.poortorich.report.response.enums.ReportResponse;
import com.poortorich.report.service.ReportService;
import com.poortorich.report.util.ReportBuilder;
import com.poortorich.report.response.ReceiptReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportFacade {

    private final ChatParticipantService chatParticipantService;
    private final ChatroomService chatroomService;
    private final ReportService reportService;

    public ReceiptReportResponse reportMember(
            String username,
            Long chatroomId,
            Long reportedId,
            ReceiptReportRequest request
    ) {
        Chatroom chatroom = chatroomService.findById(chatroomId);
        ChatParticipant reporterMember = chatParticipantService.findByUsernameAndChatroom(username, chatroom);
        ChatParticipant reportedMember = chatParticipantService.findByUserIdAndChatroom(reportedId, chatroom);

        validReporterAndReported(reporterMember, reportedMember);
        reportService.reportMember(reporterMember, reportedMember, chatroom, request);

        return ReportBuilder.buildReceiptReportResponse(reportedId, chatroomId, request.getReportType());
    }

    private void validReporterAndReported(ChatParticipant reporterMember, ChatParticipant reportedMember) {
        if (reporterMember.equals(reportedMember)) {
            throw new BadRequestException(ReportResponse.SELF_REPORT_NOT_ALLOWED);
        }
    }
}
