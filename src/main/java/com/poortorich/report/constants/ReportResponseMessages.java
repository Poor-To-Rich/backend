package com.poortorich.report.constants;

public class ReportResponseMessages {

    public static final String GET_DAILY_DETAILS_SUCCESS = "일별 상세 내역을 성공적으로 조회하였습니다.";
    public static final String GET_WEEKLY_DETAILS_SUCCESS = "주별 상세 내역을 성공적으로 조회하였습니다.";
    public static final String GET_MONTHLY_TOTAL_SUCCESS = "월별 수입/지출 합계를 성공적으로 조회하였습니다.";
    public static final String GET_MONTHLY_TOTAL_REPORT_SUCCESS = "월별 수입/지출 명세를 성공적으로 조회하였습니다.";
    public static final String GET_WEEKLY_TOTAL_REPORT_SUCCESS = "주별 수입/지출 명세를 성공적으로 조회하였습니다.";

    public static final String WEEK_INVALID = "요청한 달의 해당 주차가 존재하지 않습니다.";
    public static final String CURSOR_INVALID = "커서의 값이 요청한 날짜 안에 존재하지 않습니다.";

    private ReportResponseMessages() {
    }
}
