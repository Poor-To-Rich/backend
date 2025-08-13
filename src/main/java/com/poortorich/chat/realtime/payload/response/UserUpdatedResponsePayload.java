package com.poortorich.chat.realtime.payload.response;

import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.chat.realtime.payload.interfaces.ResponsePayload;
import com.poortorich.chat.realtime.payload.response.enums.PayloadType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdatedResponsePayload implements ResponsePayload {

    private final Long userId;
    private final String profileImage;
    private final Boolean isDefaultProfile;
    private final String nickname;
    private final Boolean isHost;
    private final RankingStatus rankingType;

    @Override
    public BasePayload mapToBasePayload() {
        return BasePayload.builder()
                .type(PayloadType.USER_UPDATED)
                .payload(this)
                .build();
    }
}
