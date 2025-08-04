package com.poortorich.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatroomDetailsResponse {

    private String chatroomImage;
    private String chatroomTitle;
    private Long currentMemberCount;
    private Boolean isRankingEnabled;
}
