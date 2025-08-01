package com.poortorich.websocket.stomp.interceptor;

import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.global.response.enums.GlobalResponse;
import com.poortorich.websocket.stomp.command.connect.handler.StompConnectHandler;
import com.poortorich.websocket.stomp.command.disconnect.handler.StompDisconnectHandler;
import com.poortorich.websocket.stomp.command.publish.handler.StompSendHandler;
import com.poortorich.websocket.stomp.command.subscribe.handler.StompSubscribeHandler;
import com.poortorich.websocket.stomp.command.unsubscribe.handler.StompUnsubscribeHandler;
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

    private final StompConnectHandler connectHandler;
    private final StompSubscribeHandler subscribeHandler;
    private final StompSendHandler sendHandler;
    private final StompUnsubscribeHandler unsubscribeHandler;
    private final StompDisconnectHandler disconnectHandler;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompCommand command = null;
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            command = accessor.getCommand();

            if (StompCommand.CONNECT.equals(command)) {
                connectHandler.handle(accessor);
            } else if (StompCommand.SUBSCRIBE.equals(command)) {
                subscribeHandler.handle(accessor);
            } else if (StompCommand.SEND.equals(command)) {
                sendHandler.handle(accessor);
            } else if (StompCommand.UNSUBSCRIBE.equals(command)) {
                unsubscribeHandler.handle(accessor);
            } else if (StompCommand.DISCONNECT.equals(command)) {
                disconnectHandler.handle();
            }
        } catch (Exception e) {
            log.info("[WebSocket 연결 실패] {}", e.getStackTrace()[0]);
            throw new InternalServerErrorException(GlobalResponse.INTERNAL_SERVER_EXCEPTION);
        }

        return message;
    }
}
