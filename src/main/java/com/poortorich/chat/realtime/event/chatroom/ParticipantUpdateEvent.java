package com.poortorich.chat.realtime.event.chatroom;

import com.poortorich.chat.entity.ChatParticipant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParticipantUpdateEvent {

    private final ChatParticipant participant;
}
