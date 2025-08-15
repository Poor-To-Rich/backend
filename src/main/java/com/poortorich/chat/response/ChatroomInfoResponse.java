package com.poortorich.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomInfoResponse {

    private String chatroomImage;
    private Boolean isDefaultProfile;
    private String chatroomTitle;
    private Long maxMemberCount;
    private String description;
    private List<String> hashtags;
    private Boolean isRankingEnabled;
    private String chatroomPassword;
}
