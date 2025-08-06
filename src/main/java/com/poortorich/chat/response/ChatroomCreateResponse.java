package com.poortorich.chat.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomCreateResponse {

    private Long newChatroomId;

    @JsonIgnore
    private Boolean savedRankingStatus;
}
