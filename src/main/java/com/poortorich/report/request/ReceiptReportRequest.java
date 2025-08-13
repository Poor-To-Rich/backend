package com.poortorich.report.request;

import com.poortorich.report.constants.ReportResponseMessage;
import com.poortorich.report.constants.ReportValidationConstraints;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReceiptReportRequest {

    @NotNull(message = ReportResponseMessage.REPORT_REASON_REQUIRED)
    private String reportType;

    @Size(max = ReportValidationConstraints.CUSTOM_REASON_MAX,
            message = ReportResponseMessage.CUSTOM_REASON_TOO_BIG)
    private String customReason;
}
