package com.poortorich.chatnotice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponse {

    private Long noticeId;
    private String preview;
    private String authorNickname;
    private String createdAt;
}
