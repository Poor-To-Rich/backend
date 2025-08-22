package com.poortorich.chatnotice.model;

import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chatnotice.response.ChatNoticeCreateResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticeCreateResult {

    private final ChatNoticeCreateResponse apiResponse;
    private final BasePayload broadcastPayload;
}
