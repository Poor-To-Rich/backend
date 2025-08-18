package com.poortorich.chat.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarkAllChatroomAsReadResponse {

    private List<Long> chatroomIds;
}
