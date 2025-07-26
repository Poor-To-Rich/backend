package com.poortorich.websocket.stomp.endpoint;

import java.util.List;

public class SubscribeEndpoint {

    public static final String CHATROOM_SUBSCRIBE_PREFIX = "/sub/chatroom/";

    public static final List<String> SUB_PREFIXES = List.of(CHATROOM_SUBSCRIBE_PREFIX);
}
