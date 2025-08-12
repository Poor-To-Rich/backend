package com.poortorich.report.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptReportResponse {

    private Long reportedUserId;
    private Long chatroomId;
    private String reportType;
}
