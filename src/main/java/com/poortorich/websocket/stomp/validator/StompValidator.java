package com.poortorich.websocket.stomp.validator;

import com.poortorich.auth.jwt.constants.JwtConstants;
import com.poortorich.auth.jwt.validator.JwtTokenValidator;
import com.poortorich.auth.response.enums.AuthResponse;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.global.exceptions.UnauthorizedException;
import com.poortorich.websocket.stomp.response.StompResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompValidator {

    private final JwtTokenValidator tokenValidator;

    public void validateAccessToken(String accessToken) {
        if (!Objects.isNull(accessToken) && accessToken.startsWith(JwtConstants.TOKEN_PREFIX)) {
            accessToken = accessToken.substring(JwtConstants.TOKEN_PREFIX.length());
            tokenValidator.validateToken(accessToken);
            return;
        }
        throw new UnauthorizedException(AuthResponse.TOKEN_INVALID);
    }

    public void validateSessionAttribute(StompHeaderAccessor accessor) {
        if (Objects.isNull(accessor.getSessionAttributes())) {
            throw new BadRequestException(StompResponse.SESSION_ATTRIBUTE_INVALID);
        }
    }

    public void validateDestination(StompHeaderAccessor accessor) {
        if (Objects.isNull(accessor.getDestination())) {
            throw new NotFoundException(StompResponse.DESTINATION_NOT_FOUND);
        }
    }
}
