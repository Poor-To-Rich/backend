package com.poortorich.chatnotice.response;

import com.poortorich.chat.response.ProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDetailsResponse {

    private Long noticeId;
    private String content;
    private String createdAt;
    private ProfileResponse author;
}
