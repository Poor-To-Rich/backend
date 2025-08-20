package com.poortorich.chatnotice.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatNoticeUpdateResponse {

    private final Long noticeId;
}
