package com.poortorich.chatnotice.model;


import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chatnotice.response.ChatNoticeUpdateResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticeUpdateResult {

    private final ChatNoticeUpdateResponse apiResponse;
    private final BasePayload broadcastPayload;
}
