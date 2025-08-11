package com.poortorich.chat.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatroomUpdateResponse {

    private final Long chatroomId;

    @JsonIgnore
    private final Boolean isChangedRankingStatus;
}
