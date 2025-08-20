package com.poortorich.chatnotice.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatNoticeCreateResponse {

    private final Long noticeId;
}
