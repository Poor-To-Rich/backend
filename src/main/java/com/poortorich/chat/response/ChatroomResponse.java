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
public class ChatroomResponse {

    private Long chatroomId;
    private String chatroomTitle;
    private String chatroomImage;
    private String description;
    private List<String> hashtags;
    private Long currentMemberCount;
    private Long maxMemberCount;
    private String lastMessageTime;
}
