package com.poortorich.chat.realtime.payload.response;

import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.model.ChatMessageResponse;
import com.poortorich.chat.realtime.payload.interfaces.ResponsePayload;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RankingStatusMessagePayload extends ChatMessageResponse implements ResponsePayload {

    private Long messageId;
    private Long chatroomId;
    private Boolean isRankingEnabled;
    private LocalDateTime sentAt;

    @Override
    public BasePayload mapToBasePayload() {
        return BasePayload.builder()
                .type(ChatMessageType.RANKING_STATUS)
                .payload(this)
                .build();
    }
}
