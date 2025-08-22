package com.poortorich.chat.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poortorich.chat.entity.ChatParticipant;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
public class HostDelegationResponse {

    private Long newHostUserId;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ChatParticipant prevHost;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ChatParticipant newHost;
}
