package com.poortorich.auth.jwt.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.poortorich.auth.jwt.constants.JwtConstants;
import com.poortorich.auth.jwt.fixture.JwtTokenFixture;
import com.poortorich.auth.jwt.fixture.JwtUserFixture;
import com.poortorich.user.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtTokenExtractorTest {

    @InjectMocks
    private JwtTokenExtractor tokenExtractor;

    private User testUser;
    private String testToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenExtractor, "secretKey", JwtTokenFixture.testSecretKey);

        testUser = User.builder()
                .id(JwtUserFixture.TEST_ID)
                .username(JwtUserFixture.TEST_USERNAME)
                .email(JwtUserFixture.TEST_EMAIL)
                .build();

        testToken = JwtTokenFixture.getValidTestAccessToken(testUser);
    }

    @Test
    @DisplayName("액세스 토큰에서 사용자 ID 추출 - 올바른 사용자 ID를 반환해야 함")
    void extractUserId_ShouldReturnCorrectUserId() {
        Long extractedUserId = tokenExtractor.extractUserId(testToken);

        assertThat(extractedUserId).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("액세스 토큰에서 사용자명 추출 - 올바른 사용자명을 반환해야 함")
    void extractUsername_ShouldReturnCorrectUsername() {
        String extractedUsername = tokenExtractor.extractUsername(testToken);

        assertThat(extractedUsername).isEqualTo(testUser.getUsername());
    }

    @Test
    @DisplayName("액세스 토큰에서 사용자 이메일 추출 - 올바른 이메일을 반환해야 함")
    void extractEmail_ShouldReturnCorrectEmail() {
        String extractedEmail = tokenExtractor.extractEmail(testToken);

        assertThat(extractedEmail).isEqualTo(testUser.getEmail());
    }

    @Test
    @DisplayName("토큰에서 만료 날짜 추출 - 올바른 만료 날짜를 반환해야 함")
    void extractExpirationDate_ShouldReturnCorrectExpirationDate() {
        Date expectedExpirationTime = new Date((new Date()).getTime() + JwtConstants.ACCESS_TOKEN_EXPIRATION_TIME);
        Date extractedExpirationTime = tokenExtractor.extractExpirationDate(testToken);

        assertThat(Math.abs(extractedExpirationTime.getTime() - expectedExpirationTime.getTime()))
                .isLessThan(JwtTokenFixture.EXPIRATION_TOLERANCE_MS);
    }

    @Test
    @DisplayName("잘못된 토큰으로 정보 추출 시 예외 발생")
    void extractInformation_ShouldThrowException_WhenTokenInvalid() {
        String invalidToken = testToken + "invalid";

        assertThatThrownBy(() -> tokenExtractor.extractUserId(invalidToken))
                .isInstanceOf(JwtException.class);

        assertThatThrownBy(() -> tokenExtractor.extractUsername(invalidToken))
                .isInstanceOf(JwtException.class);

        assertThatThrownBy(() -> tokenExtractor.extractEmail(invalidToken))
                .isInstanceOf(JwtException.class);

        assertThatThrownBy(() -> tokenExtractor.extractExpirationDate(invalidToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("만료된 토큰으로 정보 추출 시 예외 발생")
    void extractInformation_ShouldThrowException_WhenTokenIsExpired() {
        String expiredToken = JwtTokenFixture.getExpiredTestAccessToken(testUser);
        assertThatThrownBy(() -> tokenExtractor.extractUserId(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);

        assertThatThrownBy(() -> tokenExtractor.extractUsername(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);

        assertThatThrownBy(() -> tokenExtractor.extractEmail(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);

        assertThatThrownBy(() -> tokenExtractor.extractExpirationDate(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
