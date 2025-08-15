package com.poortorich.chat.model;

import com.poortorich.chat.realtime.payload.response.BasePayload;
import com.poortorich.chat.response.MarkAllChatroomAsReadResponse;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MarkAllChatroomAsReadResult {

    private final MarkAllChatroomAsReadResponse apiResponse;
    private final List<BasePayload> broadcastPayloads;
}
