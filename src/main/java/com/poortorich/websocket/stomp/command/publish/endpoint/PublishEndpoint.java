package com.poortorich.websocket.stomp.command.publish.endpoint;

import java.util.List;

public class PublishEndpoint {

    public static final String CHAT_MESSAGE_PUBLISH_PREFIX = "/pub/chat/messages";
    public static final String CHAT_MESSAGE_READ_PUBLISH_PREFIX = "/pub/chat/read";
    public static final String CHAT_NOTICE_PUBLISH_PREFIX = "/pub/chat/notices";

    public static final List<String> PUBLISH_PREFIXES = List.of(
            CHAT_MESSAGE_PUBLISH_PREFIX,
            CHAT_MESSAGE_READ_PUBLISH_PREFIX,
            CHAT_NOTICE_PUBLISH_PREFIX);
}
