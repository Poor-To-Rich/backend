package com.poortorich.chatnotice.util;

import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.response.LatestNoticeResponse;

public class ChatNoticeBuilder {

    private static final int PREVIEW_MAX_LENGTH = 30;

    public static LatestNoticeResponse buildLatestNoticeResponse(NoticeStatus status, ChatNotice notice) {
        return LatestNoticeResponse.builder()
                .status(status.toString())
                .noticeId(notice.getId())
                .preview(truncateContent(notice.getContent()))
                .createdAt(notice.getCreatedDate().toString())
                .authorNickname(notice.getAuthor().getNickname())
                .build();
    }

    private static String truncateContent(String content) {
        if (content.length() > PREVIEW_MAX_LENGTH) {
            return content.substring(0, PREVIEW_MAX_LENGTH);
        }

        return content;
    }
}
