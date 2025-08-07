package com.poortorich.chat.constants;

public class ChatResponseMessage {

    public static final String CREATE_CHATROOM_SUCCESS = "채팅방을 성공적으로 추가했습니다.";
    public static final String GET_CHATROOM_SUCCESS = "채팅방을 성공적으로 조회했습니다.";
    public static final String GET_ALL_CHATROOMS_SUCCESS = "전체 채팅방 목록 조회가 완료되었습니다.";
    public static final String GET_SEARCH_CHATROOMS_SUCCESS = "채팅방 검색이 완료되었습니다.";
    public static final String GET_HOSTED_CHATROOMS_SUCCESS = "나의 채팅방 목록 조회가 완료되었습니다.";
    public static final String GET_CHATROOM_DETAILS_SUCCESS = "채팅방 상세정보 조회를 완료했습니다.";
    public static final String GET_CHATROOM_COVER_INFO_SUCCESS = "채팅방 커버 정보 조회를 완료했습니다.";
    public static final String GET_CHATROOM_ROLE_SUCCESS = "현재 채팅방의 유저 역할 조회를 완료했습니다.";

    public static final String GET_CHATROOM_LIKE_STATUS_SUCCESS = "채팅방 좋아요 상태 조회를 완료했습니다.";

    public static final String CHATROOM_TITLE_REQUIRED = "채팅방 이름은 필수값입니다.";
    public static final String CHATROOM_TITLE_TOO_BIG
            = "채팅방 이름은 " + ChatValidationConstraints.CHATROOM_TITLE_MAX + "자 이하여야 합니다.";

    public static final String CHATROOM_MAX_MEMBER_COUNT_REQUIRED = "채팅방 최대 인원은 필수값입니다.";
    public static final String CHATROOM_MAX_MEMBER_COUNT_TOO_SMALL
            = "채팅방 최대 인원은 " + ChatValidationConstraints.CHATROOM_MAX_MEMBER_COUNT_MIN + "명 이상이어야 합니다.";
    public static final String CHATROOM_MAX_MEMBER_COUNT_TOO_BIG
            = "채팅방 최대 인원은 " + ChatValidationConstraints.CHATROOM_MAX_MEMBER_COUNT_MAX + "명 이하여야 합니다.";

    public static final String CHATROOM_DESCRIPTION_REQUIRED = "채팅방 소개는 필수값입니다.";
    public static final String CHATROOM_DESCRIPTION_TOO_BIG
            = "채팅방 소개는 " + ChatValidationConstraints.CHATROOM_DESCRIPTION_MAX + "자 이하여야 합니다.";

    public static final String TAG_TOO_MANY = "태그는 " + ChatValidationConstraints.TAG_COUNT_MAX + "개 이하여야 합니다.";

    public static final String CHATROOM_ENTER_DENIED = "이 채팅방에 입장할 수 없습니다.";
    public static final String CHATROOM_PASSWORD_DO_NOT_MATCH = "채팅방 비밀번호가 맞지 않습니다.";
    public static final String CHATROOM_NOT_FOUND = "채팅방을 찾을 수 없습니다.";
    public static final String CHATROOM_ENTER_SUCCESS = "채팅방 입장이 성공적으로 완료되었습니다.";
    public static final String CHATROOM_ENTER_DUPLICATED = "이미 참여중인 채팅방입니다.";
    public static final String CHATROOM_UPDATE_SUCCESS = "채팅방을 성공적으로 편집했습니다.";
    public static final String CHAT_PARTICIPANT_NOT_FOUND = "참여자를 찾을 수 없습니다.";
    public static final String CHATROOM_NOT_PARTICIPATE = "채팅방에 참여하고 있지 않습니다.";
    public static final String CHAT_PARTICIPANT_NOT_HOST = "채팅방 방장이 아닙니다.";
    public static final String CHATROOM_MAX_MEMBER_COUNT_EXCEED = "최대 인원 수를 현재 인원보다 적게 설정할 수 없습니다.";

    public static final String CHATROOM_LEAVE_ALREADY = "채팅방에 참여하고 있지 않습니다.";
    public static final String CHATROOM_LEAVE_SUCCESS = "채팅방을 성공적으로 나갔습니다.";
    public static final String CHAT_MESSAGE_NOT_FOUND = "채팅 메시지를 찾을 수 없습니다.";
    public static final String GET_CHAT_MESSAGE_SUCCESS = "이전 채팅 조회가 완료됐습니다.";

    public static final String CHATROOM_ID_REQUIRED = "채팅방 아이디는 필수값입니다.";
    public static final String MESSAGE_TPYE_REQUIRED = "메시지 타입은 필수값입니다.";

    private ChatResponseMessage() {
    }
}
