package com.poortorich.chat.realtime.payload.response;

import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.realtime.payload.interfaces.ResponsePayload;
import com.poortorich.chat.realtime.payload.response.enums.PayloadType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEnterProfileResponsePayload implements ResponsePayload {

    private Long userId;
    private String profileImage;
    private String nickname;
    private Boolean isHost;
    private RankingStatus rankingType;

    @Override
    public BasePayload mapToBasePayload() {
        return BasePayload.builder()
                .type(PayloadType.USER_JOINED)
                .payload(this)
                .build();
    }
}
