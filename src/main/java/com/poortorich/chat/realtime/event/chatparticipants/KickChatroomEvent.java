package com.poortorich.chat.realtime.event.chatparticipants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KickChatroomEvent {

    private final Long chatParticipantId;
}
