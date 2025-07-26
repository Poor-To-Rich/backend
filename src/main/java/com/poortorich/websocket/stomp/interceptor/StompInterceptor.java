package com.poortorich.websocket.interceptor;

import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.global.response.enums.GlobalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompCommand command = null;
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            command = accessor.getCommand();

            if (StompCommand.CONNECT.equals(command)) {
                log.info("[WebSocket]: 연결 시도");
            } else if (StompCommand.SUBSCRIBE.equals(command)) {
                log.info("[WebSocket]: 구독 시도");
            } else if (StompCommand.SEND.equals(command)) {
                log.info("[WebSocket]: 전송 시도");
            } else if (StompCommand.DISCONNECT.equals(command)) {
                log.info("[WebSocket]: 연결 해제 시도");
            }
        } catch (Exception e) {
            log.info("[WebSocket 연결 실패]");
            throw new InternalServerErrorException(GlobalResponse.INTERNAL_SERVER_EXCEPTION);
        }

        return message;
    }
}
