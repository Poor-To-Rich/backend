package com.poortorich.auth.jwt.util;

import com.poortorich.auth.jwt.constants.JwtConstants;
import com.poortorich.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenGenerator {

    @Value("${jwt.secret.key}")
    private String secretKey;

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JwtConstants.ACCESS_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer(JwtConstants.TOKEN_ISSUER)
                .audience().add(JwtConstants.TOKEN_AUDIENCE).and()
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JwtConstants.REFRESH_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer(JwtConstants.TOKEN_ISSUER)
                .audience().add(JwtConstants.TOKEN_AUDIENCE).and()
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
