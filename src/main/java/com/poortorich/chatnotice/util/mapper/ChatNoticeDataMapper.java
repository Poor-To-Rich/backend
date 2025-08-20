package com.poortorich.chatnotice.util.mapper;

import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chat.realtime.payload.response.enums.PayloadType;
import com.poortorich.chatnotice.entity.ChatNotice;
import com.poortorich.chatnotice.model.NoticeCreateResult;
import com.poortorich.chatnotice.model.NoticeUpdateResult;
import com.poortorich.chatnotice.response.ChatNoticeCreateResponse;
import com.poortorich.chatnotice.response.ChatNoticeUpdateResponse;
import com.poortorich.chatnotice.response.LatestNoticeResponse;
import com.poortorich.chatnotice.response.enums.ChatNoticeResponse;
import com.poortorich.chatnotice.util.ChatNoticeBuilder;
import com.poortorich.global.exceptions.InternalServerErrorException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ChatNoticeDataMapper {

    public NoticeCreateResult mapToNoticeCreateResult(ChatNotice chatNotice) {
        if (Objects.isNull(chatNotice)) {
            throw new InternalServerErrorException(ChatNoticeResponse.CHAT_NOTICE_CREATE_FAILURE);
        }

        return NoticeCreateResult.builder()
                .apiResponse(ChatNoticeCreateResponse.builder().noticeId(chatNotice.getId()).build())
                .broadcastPayload(BasePayload.builder()
                        .type(PayloadType.NOTICE)
                        .payload(ChatNoticeBuilder.buildLatestNoticeResponse(NoticeStatus.DEFAULT, chatNotice))
                        .build())
                .build();
    }

    public NoticeUpdateResult mapToNoticeUpdateResult(ChatNotice chatNotice, boolean isLatestNotice) {
        if (Objects.isNull(chatNotice)) {
            throw new InternalServerErrorException(ChatNoticeResponse.CHAT_NOTICE_UPDATE_FAILURE);
        }

        if (isLatestNotice) {
            return NoticeUpdateResult.builder()
                    .apiResponse(ChatNoticeUpdateResponse.builder().noticeId(chatNotice.getId()).build())
                    .build();
        }

        return NoticeUpdateResult.builder()
                .apiResponse(ChatNoticeUpdateResponse.builder().noticeId(chatNotice.getId()).build())
                .broadcastPayload(BasePayload.builder()
                        .type(PayloadType.NOTICE)
                        .payload(ChatNoticeBuilder.buildLatestNoticeResponse(NoticeStatus.DEFAULT, chatNotice))
                        .build())
                .build();
    }

    public BasePayload mapToNoticeDeletePayload(boolean isLatestNotice) {
        if (isLatestNotice) {
            return BasePayload.builder()
                    .type(PayloadType.NOTICE)
                    .payload(LatestNoticeResponse.builder()
                            .status(NoticeStatus.PERMANENT_HIDDEN.toString())
                            .build())
                    .build();
        }
        return null;
    }
}
