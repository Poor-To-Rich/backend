package com.poortorich.chat.realtime.event.datechange;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DateChangeEvent {

    private final Long chatroomId;
}
