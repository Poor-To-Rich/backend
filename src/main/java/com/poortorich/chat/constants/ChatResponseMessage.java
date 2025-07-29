package com.poortorich.chat.constants;

public class ChatResponseMessage {

    public static final String CREATE_CHATROOM_SUCCESS = "채팅방을 성공적으로 추가했습니다.";
    public static final String GET_CHATROOM_SUCCESS = "채팅방을 성공적으로 조회했습니다.";

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

    public static final String CHATROOM_ENTER_DENIED = "이 채팅방에 입장할 수 없습니다.";
    public static final String CHATROOM_PASSWORD_DO_NOT_MATCH = "채팅방 비밀번호가 맞지 않습니다.";
    public static final String CHATROOM_NOT_FOUND = "채팅방을 찾을 수 없습니다.";
    public static final String CHATROOM_ENTER_SUCCESS = "채팅방 입장이 성공적으로 완료되었습니다.";
    public static final String CHATROOM_ENTER_DUPLICATED = "이미 참여중인 채팅방입니다.";

    private ChatResponseMessage() {
    }
}
