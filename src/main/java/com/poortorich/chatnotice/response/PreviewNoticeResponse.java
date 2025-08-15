package com.poortorich.chatnotice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviewNoticeResponse {

    private Long noticeId;
    private String preview;
}
