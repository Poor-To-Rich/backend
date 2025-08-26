package com.poortorich.websocket.stomp.command.subscribe.endpoint;

import java.util.List;

public class SubscribeEndpoint {

    public static final String CHATROOM_SUBSCRIBE_PREFIX = "/sub/chatroom/";
    public static final String JOINED_CHATROOM_LIST_PREFIX = "/sub/chat/summary";

    public static final List<String> SUB_PREFIXES = List.of(CHATROOM_SUBSCRIBE_PREFIX, JOINED_CHATROOM_LIST_PREFIX);
}
