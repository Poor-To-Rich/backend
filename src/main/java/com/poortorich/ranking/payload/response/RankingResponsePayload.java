package com.poortorich.ranking.payload.response;

import com.poortorich.chat.entity.enums.ChatMessageType;
import com.poortorich.chat.entity.enums.MessageType;
import com.poortorich.chat.model.ChatMessageResponse;
import com.poortorich.chat.realtime.payload.interfaces.ResponsePayload;
import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chat.response.ChatParticipantProfile;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RankingResponsePayload extends ChatMessageResponse implements ResponsePayload {

    private Long messageId;
    private Long rankingId;
    private Long chatroomId;
    private LocalDate rankedAt;
    private LocalDateTime sentAt;
    private List<ChatParticipantProfile> saverRankings;
    private List<ChatParticipantProfile> flexerRankings;
    private MessageType messageType;
    private ChatMessageType type;

    @Override
    public BasePayload mapToBasePayload() {
        return BasePayload.builder()
                .type(ChatMessageType.RANKING_MESSAGE)
                .payload(this)
                .build();
    }
}
