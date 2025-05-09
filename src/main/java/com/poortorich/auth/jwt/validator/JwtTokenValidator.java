package com.poortorich.auth.jwt.validator;

import com.poortorich.auth.response.enums.AuthResponse;
import com.poortorich.global.exceptions.UnauthorizedException;
import com.poortorich.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final UserRepository userRepository;

    @Value("${jwt.secret.key}")
    private String secretKey;

    public void validateToken(String token) {
        try {
            Claims claims = Jwts
                    .parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();

            userRepository.findByUsername(username)
                    .orElseThrow(() -> new UnauthorizedException(AuthResponse.TOKEN_INVALID));

        } catch (SecurityException | MalformedJwtException |
                 ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException | SignatureException e) {
            throw new UnauthorizedException(AuthResponse.TOKEN_INVALID);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwts
                    .parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SecurityException | MalformedJwtException |
                 UnsupportedJwtException | IllegalArgumentException |
                 SignatureException e) {
            throw new UnauthorizedException(AuthResponse.TOKEN_INVALID);
        } catch (ExpiredJwtException e) {
            return true;
        }
        return false;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
