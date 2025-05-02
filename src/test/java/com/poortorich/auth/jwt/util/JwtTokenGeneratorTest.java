package com.poortorich.auth.jwt.util;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.poortorich.auth.jwt.constants.JwtConstants;
import com.poortorich.auth.jwt.fixture.JwtTokenFixture;
import com.poortorich.auth.jwt.fixture.JwtUserFixture;
import com.poortorich.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtTokenGeneratorTest {

    @InjectMocks
    private JwtTokenGenerator tokenGenerator;

    private User testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenGenerator, "secretKey", JwtTokenFixture.testSecretKey);

        testUser = User.builder()
                .id(JwtUserFixture.TEST_ID)
                .username(JwtUserFixture.TEST_USERNAME)
                .email(JwtUserFixture.TEST_EMAIL)
                .build();
    }

    @Test
    @DisplayName("액세스 토큰 생성 - 유효한 토큰이 생성되어야 함")
    void generateAccessToken_ShouldCreateValidToken() {
        String accessToken = tokenGenerator.generateAccessToken(testUser);

        assertThat(accessToken).isNotEmpty();

        Claims claims = Jwts.parser()
                .verifyWith(getTestSigningKey())
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(testUser.getUsername());
        assertThat(((Number) claims.get("userId")).longValue()).isEqualTo(testUser.getId());
        assertThat(claims.get("email")).isEqualTo(testUser.getEmail());
        assertThat(claims.getIssuer()).isEqualTo(JwtConstants.TOKEN_ISSUER);
        assertThat(claims.getAudience()).contains(JwtConstants.TOKEN_AUDIENCE);

        assertValidExpirationTime(claims, JwtConstants.ACCESS_TOKEN_EXPIRATION_TIME);
    }

    @Test
    @DisplayName("리프레시 토큰 생성 - 유효한 토큰이 생성되어야 함")
    void generateRefreshToken_ShouldCreateValidToken() {
        String refreshToken = tokenGenerator.generateRefreshToken(testUser);

        assertThat(refreshToken).isNotEmpty();

        Claims claims = Jwts.parser()
                .verifyWith(getTestSigningKey())
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(testUser.getUsername());
        assertThat(claims.getIssuer()).isEqualTo(JwtConstants.TOKEN_ISSUER);
        assertThat(claims.getAudience()).contains(JwtConstants.TOKEN_AUDIENCE);

        assertValidExpirationTime(claims, JwtConstants.REFRESH_TOKEN_EXPIRATION_TIME);
    }

    @Test
    @DisplayName("토큰 만료시간 비교 - 리프레시 토큰 만료시간이 액세스 토큰보다 길어야 함")
    void tokensShouldHaveDifferentExpirationTimes() {
        String accessToken = tokenGenerator.generateAccessToken(testUser);
        String refreshToken = tokenGenerator.generateRefreshToken(testUser);

        Claims accessClaims = Jwts.parser()
                .verifyWith(getTestSigningKey())
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();

        Claims refreshClaims = Jwts.parser()
                .verifyWith(getTestSigningKey())
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        Date accessExpiration = accessClaims.getExpiration();
        Date refreshExpiration = refreshClaims.getExpiration();

        assertThat(refreshExpiration).isAfter(accessExpiration);
    }

    private SecretKey getTestSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(JwtTokenFixture.testSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private void assertValidExpirationTime(Claims claims, long expirationTime) {
        Date now = new Date();
        assertThat(claims.getExpiration()).isAfter(now);

        long expectedExpirationTime = claims.getIssuedAt().getTime() + expirationTime;
        long actualExpirationTime = claims.getExpiration().getTime();
        assertThat(Math.abs(expectedExpirationTime - actualExpirationTime))
                .isLessThan(JwtTokenFixture.EXPIRATION_TOLERANCE_MS);
    }
}
