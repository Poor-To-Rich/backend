package com.poortorich.chat.realtime.event.user;

import com.poortorich.chat.entity.ChatParticipant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HostDelegationEvent {

    private final ChatParticipant prevHost;
    private final ChatParticipant newHost;
}
