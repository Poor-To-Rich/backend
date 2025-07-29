package com.poortorich.chat.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatroomEnterRequest {

    private final String chatroomPassword;
}
