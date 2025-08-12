package com.poortorich.report.request;

import com.poortorich.report.constants.ReportResponseMessage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReceiptReportRequest {

    @NotNull(message = ReportResponseMessage.REPORT_REASON_REQUIRED)
    private String reportType;
    private String customReason;
}
