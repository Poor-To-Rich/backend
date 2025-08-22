package com.poortorich.chat.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poortorich.chat.entity.ChatParticipant;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
public class KickChatParticipantResponse {

    private Long kickUserId;

    @JsonIgnore
    @ToString.Exclude
    private ChatParticipant kickChatParticipant;
}
