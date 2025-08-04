package com.poortorich.chat.response;

import com.poortorich.chat.entity.enums.RankingStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatParticipantProfile {

    private final Long userId;
    private final String nickname;
    private final String profileImage;
    private final RankingStatus rankingType;
    private final Boolean isHost;
}
