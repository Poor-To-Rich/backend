package com.poortorich.websocket.stomp.command.publish.endpoint;

import java.util.List;

public class PublishEndpoint {

    public static final String CHAT_MESSAGE_PUBLISH_PREFIX = "/pub/chat/messages";

    public static final List<String> PUBLISH_PREFIXES = List.of(CHAT_MESSAGE_PUBLISH_PREFIX);
}
