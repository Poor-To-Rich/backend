package com.poortorich.chatnotice.util;

import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.util.ChatBuilder;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.response.LatestNoticeResponse;
import com.poortorich.chatnotice.response.NoticeDetailsResponse;
import com.poortorich.chatnotice.response.PreviewNoticeResponse;

import java.util.List;
import java.util.Objects;

public class ChatNoticeBuilder {

    private static final int PREVIEW_MAX_LENGTH = 30;

    public static LatestNoticeResponse buildLatestNoticeResponse(NoticeStatus status, ChatNotice notice) {
        return LatestNoticeResponse.builder()
                .status(status.name())
                .noticeId(notice.getId())
                .preview(truncateContent(notice.getContent()))
                .createdAt(notice.getCreatedDate().toString())
                .authorNickname(notice.getAuthor().getUser().getNickname())
                .build();
    }

    public static List<PreviewNoticeResponse> buildPreviewNoticeResponse(List<ChatNotice> previewNotices) {
        return previewNotices.stream()
                .filter(Objects::nonNull)
                .map(notice -> PreviewNoticeResponse.builder()
                        .noticeId(notice.getId())
                        .preview(truncateContent(notice.getContent()))
                        .build())
                .toList();
    }

    private static String truncateContent(String content) {
        if (content.length() > PREVIEW_MAX_LENGTH) {
            return content.substring(0, PREVIEW_MAX_LENGTH);
        }

        return content;
    }

    public static NoticeDetailsResponse buildNoticeDetailsResponse(ChatNotice notice) {
        return NoticeDetailsResponse.builder()
                .noticeId(notice.getId())
                .content(notice.getContent())
                .createdAt(notice.getCreatedDate().toString())
                .author(ChatBuilder.buildProfileResponse(notice.getAuthor()))
                .build();
    }
}
