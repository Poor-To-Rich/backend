package com.poortorich.auth.jwt.validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.poortorich.auth.jwt.fixture.JwtTokenFixture;
import com.poortorich.auth.jwt.fixture.JwtUserFixture;
import com.poortorich.auth.response.enums.AuthResponse;
import com.poortorich.global.exceptions.UnauthorizedException;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtTokenValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtTokenValidator tokenValidator;

    private User testUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenValidator, "secretKey", JwtTokenFixture.testSecretKey);

        testUser = mock(User.class);
        when(testUser.getUsername()).thenReturn(JwtUserFixture.TEST_USERNAME);

        validToken = JwtTokenFixture.getValidTestAccessToken(testUser);
    }

    @Test
    @DisplayName("유효한 토큰 검증 - 예외가 발생하지 않아야 함")
    void validateToken_ShouldNotThrowException_WhenTokenIsValid() {
        when(userRepository.findByUsername(JwtUserFixture.TEST_USERNAME)).thenReturn(Optional.of(testUser));

        assertThatCode(() -> tokenValidator.validateToken(validToken))
                .doesNotThrowAnyException();

        verify(userRepository).findByUsername(testUser.getUsername());
    }

    @Test
    @DisplayName("만료된 토큰 검증 - UnauthorizedException 예외가 발생해야 함")
    void validateToken_ShouldThrowException_WhenTokenIsExpired() {
        String expiredToken = JwtTokenFixture.getExpiredTestAccessToken(testUser);

        assertThatThrownBy(() -> tokenValidator.validateToken(expiredToken))
                .isInstanceOf(UnauthorizedException.class)
                .extracting(e -> ((UnauthorizedException) e).getResponse())
                .isEqualTo(AuthResponse.TOKEN_INVALID);

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("형식이 잘못된 토큰 검증 - UnauthorizedException 예외가 발생해야 함")
    void validateToken_ShouldThrowException_WhenTokenIsMalformed() {
        String malformedToken = validToken + "invalid";

        assertThatThrownBy(() -> tokenValidator.validateToken(malformedToken))
                .isInstanceOf(UnauthorizedException.class)
                .extracting(e -> ((UnauthorizedException) e).getResponse())
                .isEqualTo(AuthResponse.TOKEN_INVALID);

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("사용자가 존재하지 않는 토큰 검증 - UnauthorizedException 예외가 발생해야 함")
    void validateToken_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tokenValidator.validateToken(validToken))
                .isInstanceOf(UnauthorizedException.class)
                .extracting(e -> ((UnauthorizedException) e).getResponse())
                .isEqualTo(AuthResponse.TOKEN_INVALID);

        verify(userRepository).findByUsername(testUser.getUsername());
    }

    @Test
    @DisplayName("만료 확인 - 유효한 토큰은 만료되지 않아야 함")
    void isTokenExpired_ShouldReturnFalse_WhenTokenIsValid() {
        assertThat(tokenValidator.isTokenExpired(validToken)).isFalse();
    }

    @Test
    @DisplayName("만료 확인 - 만료된 토큰은 만료되어야 함")
    void isTokenExpired_ShouldReturnTrue_WhenTokenIsExpired() {
        String expiredToken = JwtTokenFixture.getExpiredTestAccessToken(testUser);

        assertThat(tokenValidator.isTokenExpired(expiredToken)).isTrue();
    }

    @Test
    @DisplayName("만료 확인 - 형식이 잘못된 토큰은 예외를 발생시켜야 함")
    void isTokenExpired_ShouldThrowException_WhenTokenIsMalformed() {
        String malformedToken = validToken + "invalid";

        assertThatThrownBy(() -> tokenValidator.isTokenExpired(malformedToken))
                .isInstanceOf(UnauthorizedException.class)
                .extracting(e -> ((UnauthorizedException) e).getResponse())
                .isEqualTo(AuthResponse.TOKEN_INVALID);
    }

    @Test
    @DisplayName("만료 확인 - null 토큰은 예외를 발생시켜야 함")
    void isTokenExpired_ShouldThrowException_WhenTokenIsNull() {
        assertThatThrownBy(() -> tokenValidator.isTokenExpired(null))
                .isInstanceOf(UnauthorizedException.class)
                .extracting(e -> ((UnauthorizedException) e).getResponse())
                .isEqualTo(AuthResponse.TOKEN_INVALID);
    }
}
