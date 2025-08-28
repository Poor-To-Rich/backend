package com.poortorich.chat.realtime.event.chatroom;

import com.poortorich.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserChatroomUpdateEvent {

    private final User user;
}
