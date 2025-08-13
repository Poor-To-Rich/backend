package com.poortorich.chat.realtime.payload.response;

import com.poortorich.chat.entity.enums.RankingStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdatedResponsePayload {

    private final Long userId;
    private final String profileImage;
    private final Boolean isDefaultProfile;
    private final Boolean isHost;
    private final RankingStatus rankingType;
}
