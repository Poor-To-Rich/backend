package com.poortorich.chatnotice.util;

import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.util.mapper.ParticipantProfileMapper;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.response.AllNoticesResponse;
import com.poortorich.chatnotice.response.LatestNoticeResponse;
import com.poortorich.chatnotice.response.NoticeDetailsResponse;
import com.poortorich.chatnotice.response.NoticeResponse;
import com.poortorich.chatnotice.response.PreviewNoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ChatNoticeBuilder {

    private static final int PREVIEW_MAX_LENGTH = 30;

    private final ParticipantProfileMapper profileMapper;

    public AllNoticesResponse buildAllNoticesResponse(
            Boolean hasNext,
            Long nextCursor,
            List<ChatNotice> chatNotices
    ) {
        return AllNoticesResponse.builder()
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .notices(chatNotices == null ? List.of() : buildNoticeResponses(chatNotices))
                .build();
    }

    private List<NoticeResponse> buildNoticeResponses(List<ChatNotice> chatNotices) {
        return chatNotices.stream()
                .filter(Objects::nonNull)
                .map(notice -> NoticeResponse.builder()
                        .noticeId(notice.getId())
                        .preview(truncateContent(notice.getContent()))
                        .authorNickname(notice.getAuthor().getUser().getNickname())
                        .createdAt(notice.getCreatedDate().toString())
                        .build())
                .toList();
    }

    public LatestNoticeResponse buildLatestNoticeResponse(NoticeStatus status, ChatNotice notice) {
        if (notice == null) {
            return null;
        }

        return LatestNoticeResponse.builder()
                .status(status.name())
                .noticeId(notice.getId())
                .preview(truncateContent(notice.getContent()))
                .createdAt(notice.getCreatedDate().toString())
                .authorNickname(notice.getAuthor().getUser().getNickname())
                .build();
    }

    public List<PreviewNoticeResponse> buildPreviewNoticeResponse(List<ChatNotice> previewNotices) {
        return previewNotices.stream()
                .filter(Objects::nonNull)
                .map(notice -> PreviewNoticeResponse.builder()
                        .noticeId(notice.getId())
                        .preview(truncateContent(notice.getContent()))
                        .build())
                .toList();
    }

    public NoticeDetailsResponse buildNoticeDetailsResponse(ChatNotice notice) {
        return NoticeDetailsResponse.builder()
                .noticeId(notice.getId())
                .content(notice.getContent())
                .createdAt(notice.getCreatedDate().toString())
                .author(profileMapper.mapToProfile(notice.getAuthor()))
                .build();
    }

    private String truncateContent(String content) {
        if (content.length() > PREVIEW_MAX_LENGTH) {
            return content.substring(0, PREVIEW_MAX_LENGTH);
        }

        return content;
    }
}
