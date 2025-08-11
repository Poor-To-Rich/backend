package com.poortorich.chat.constants;

public class ChatValidationConstraints {
    
    public static final int CHATROOM_TITLE_MAX = 30;
    
    public static final int CHATROOM_MAX_MEMBER_COUNT_MIN = 10;
    public static final int CHATROOM_MAX_MEMBER_COUNT_MAX = 100;

    public static final int CHATROOM_DESCRIPTION_MAX = 100;

    public static final int TAG_COUNT_MAX = 10;
    
    private ChatValidationConstraints() {
    }
}
