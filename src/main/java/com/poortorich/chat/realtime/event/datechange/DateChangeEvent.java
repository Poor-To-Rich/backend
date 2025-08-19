package com.poortorich.chat.realtime.event.datechange;

import com.poortorich.chat.entity.Chatroom;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DateChangeEvent {

    private final Chatroom chatroom;
}
