package com.poortorich.chat.realtime.controller;

import com.poortorich.chat.realtime.facade.ChatRealTimeFacade;
import com.poortorich.websocket.stomp.util.StompSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatRealtimeController {

    private final ChatRealTimeFacade chatRealTimeFacade;

    private final SimpMessagingTemplate messagingTemplate;
    private final StompSessionManager sessionManager;

}
