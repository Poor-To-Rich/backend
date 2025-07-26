package com.poortorich.websocket.stomp.command.connect.handler;

import com.poortorich.auth.jwt.constants.JwtConstants;
import com.poortorich.auth.jwt.util.JwtTokenExtractor;
import com.poortorich.websocket.stomp.util.StompHeaderExtractor;
import com.poortorich.websocket.stomp.util.StompSessionManager;
import com.poortorich.websocket.stomp.validator.StompValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompConnectHandler {

    private final StompHeaderExtractor headerExtractor;
    private final StompSessionManager sessionManager;
    private final StompValidator stompValidator;

    private final JwtTokenExtractor tokenExtractor;

    public void handle(StompHeaderAccessor accessor) {
        log.info("[CONNECT]: 연결 시작");
        log.info("[CONNECT]: AccessToken 검증 시작");
        String accessToken = headerExtractor.extractAccessToken(accessor);
        stompValidator.validateAccessToken(accessToken);
        log.info("[CONNECT]: AccessToken 검증 성공");
        log.info("[CONNECT]: Session에 username 저장 시작");
        String username = tokenExtractor.extractUsername(accessToken.substring(JwtConstants.TOKEN_PREFIX.length()));
        sessionManager.setUsername(accessor, username);
        log.info("[CONNECT]: Session에 username 저장 성공");
        log.info("[CONNECT] 연결 완료, 사용자: {}, 세션 아이디: {}", username, accessor.getSessionId());
    }
}
