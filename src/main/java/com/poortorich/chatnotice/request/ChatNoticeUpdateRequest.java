package com.poortorich.chatnotice.request;

import com.poortorich.chatnotice.constants.ChatNoticeResponseMessage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatNoticeUpdateRequest {

    @NotNull(message = ChatNoticeResponseMessage.NOTICE_CONTENT_REQUIRED)
    private final String content;
}
