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
public class ChatroomCoverInfoResponse {

    private Long chatroomId;
    private String chatroomTitle;
    private String chatroomImage;
    private String description;
    private List<String> hashtags;
    private Long currentMemberCount;
    private Long maxMemberCount;
    private String createdAt;
    private Boolean isJoined;
    private Boolean hasPassword;
    private ChatParticipantProfile hostProfile;
}
