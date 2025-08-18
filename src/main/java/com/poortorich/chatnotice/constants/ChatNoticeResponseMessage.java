package com.poortorich.chatnotice.constants;

public class ChatNoticeResponseMessage {

    public static final String GET_LATEST_NOTICE_SUCCESS = "최근 공지 조회를 완료했습니다.";
    public static final String UPDATE_NOTICE_STATUS_SUCCESS = "공지 상태 변경이 완료되었습니다.";
    public static final String GET_PREVIEW_NOTICE_SUCCESS = "최신 공지 목록 조회를 완료했습니다.";

    public static final String NOTICE_STATUS_REQUIRED = "공지 상태는 필수값입니다.";
    public static final String NOTICE_STATUS_INVALID = "공지 상태가 적절하지 않습니다.";

    public static final String NOTICE_NOT_FOUND = "공지를 찾을 수 없습니다.";
    public static final String NOTICE_TYPE_REQUIRED = "공지 처리 유형은 필수 값입니다.";

    private ChatNoticeResponseMessage() {
    }
}
