package com.poortorich.report.constants;

public class ReportResponseMessage {

    public static final String MEMBER_REPORT_SUCCESS = "신고가 정상적으로 접수되었습니다.";

    public static final String REPORT_REASON_REQUIRED = "신고 사유는 필수입니다.";
    public static final String CUSTOM_REASON_TOO_BIG
            = "기타 사유는 " + ReportValidationConstraints.CUSTOM_REASON_MAX + "자 이하여야 합니다.";

    public static final String REPORT_DUPLICATE = "현재 채팅방에서 이미 동일 사유로 이 참여자의 신고가 접수되었습니다.";
    public static final String SELF_REPORT_NOT_ALLOWED = "자기 자신을 신고할 수 없습니다.";
    public static final String REPORT_REASON_INVALID = "신고 사유가 적절하지 않습니다.";

    private ReportResponseMessage() {
    }
}
