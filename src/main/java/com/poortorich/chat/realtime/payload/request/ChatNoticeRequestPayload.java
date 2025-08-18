package com.poortorich.chat.realtime.payload.request;

import com.poortorich.chat.constants.ChatResponseMessage;
import com.poortorich.chat.realtime.payload.request.enums.NoticeType;
import com.poortorich.chatnotice.constants.ChatNoticeResponseMessage;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatNoticeRequestPayload {

    @NotNull(message = ChatResponseMessage.CHATROOM_ID_REQUIRED)
    private final Long chatroomId;

    private final String content;

    @NotNull(message = ChatNoticeResponseMessage.NOTICE_TYPE_REQUIRED)
    private final NoticeType noticeType;
}
