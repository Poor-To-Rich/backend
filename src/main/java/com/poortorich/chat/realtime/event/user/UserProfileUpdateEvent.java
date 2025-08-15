package com.poortorich.chat.realtime.event.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileUpdateEvent {

    private final String username;
}
