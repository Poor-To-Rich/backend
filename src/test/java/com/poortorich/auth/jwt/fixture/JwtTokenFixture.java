package com.poortorich.auth.jwt.fixture;

import com.poortorich.auth.jwt.constants.JwtConstants;
import com.poortorich.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;

public class JwtTokenFixture {

    public static final String testSecretKey = "askdmwalkIIsdawsdmdkasKwPQowelasdsadwwisdawkmdowiaWIAdawmkldsa";
    public static final long EXPIRATION_TOLERANCE_MS = 1000L;

    public static String getValidTestAccessToken(User user) {
        Date now = new Date();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + JwtConstants.ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(getTestSigningKey())
                .compact();
    }

    public static String getExpiredTestAccessToken(User user) {
        Date now = new Date();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .issuedAt(now)
                .expiration(now)
                .signWith(getTestSigningKey())
                .compact();
    }

    private static SecretKey getTestSigningKey() {
        byte[] keyByte = Base64.getDecoder().decode(testSecretKey);
        return Keys.hmacShaKeyFor(keyByte);
    }
}
