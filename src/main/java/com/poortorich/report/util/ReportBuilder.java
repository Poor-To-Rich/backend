package com.poortorich.report.util;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.report.entity.Report;
import com.poortorich.report.entity.enums.ReportReason;
import com.poortorich.report.response.ReceiptReportResponse;

public class ReportBuilder {

    public static Report buildReport(
            ChatParticipant reporterUser,
            ChatParticipant reportedUser,
            Chatroom chatroom,
            ReportReason reason,
            String customReason
    ) {
        return Report.builder()
                .reporter(reporterUser)
                .reported(reportedUser)
                .chatroom(chatroom)
                .reason(reason)
                .customReason(customReason)
                .build();
    }

    public static ReceiptReportResponse buildReceiptReportResponse(
            Long reportedUserId,
            Long chatroomId,
            String reportReason
    ) {
        return ReceiptReportResponse.builder()
                .reportedUserId(reportedUserId)
                .chatroomId(chatroomId)
                .reportReason(reportReason)
                .build();
    }
}
