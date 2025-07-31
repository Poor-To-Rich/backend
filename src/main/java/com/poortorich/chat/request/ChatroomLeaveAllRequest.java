package com.poortorich.chat.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatroomLeaveAllRequest {

    private List<Long> chatroomsToLeave;
}
