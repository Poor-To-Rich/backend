package com.poortorich.chat.model;

import com.poortorich.chat.realtime.payload.response.MessageReadPayload;
import com.poortorich.chat.response.MarkAllChatroomAsReadResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MarkAllChatroomAsReadResult {

    private final MarkAllChatroomAsReadResponse apiResponse;
    private final List<MessageReadPayload> broadcastPayloads;
}
