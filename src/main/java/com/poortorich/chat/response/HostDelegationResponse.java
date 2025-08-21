package com.poortorich.chat.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poortorich.chat.entity.ChatParticipant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HostDelegationResponse {

    private Long newHostUserId;

    @JsonIgnore
    private ChatParticipant prevHost;

    @JsonIgnore
    private ChatParticipant newHost;
}
