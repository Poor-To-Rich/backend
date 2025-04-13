package com.poortorich.auth.service;

import com.poortorich.auth.jwt.util.JwtCookieManager;
import com.poortorich.auth.jwt.util.JwtTokenExtractor;
import com.poortorich.auth.jwt.util.JwtTokenGenerator;
import com.poortorich.auth.jwt.validator.JwtTokenValidator;
import com.poortorich.auth.repository.interfaces.RefreshTokenRepository;
import com.poortorich.auth.request.LoginRequest;
import com.poortorich.auth.response.enums.AuthResponse;
import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.global.exceptions.UnauthorizedException;
import com.poortorich.global.response.Response;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator tokenGenerator;
    private final JwtTokenValidator tokenValidator;
    private final JwtTokenExtractor tokenExtractor;
    private final JwtCookieManager cookieManager;

    public Response login(LoginRequest loginRequest, HttpServletResponse response) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            return AuthResponse.CREDENTIALS_INVALID;
        }

        User user = (User) userDetails;

        String accessToken = tokenGenerator.generateAccessToken(user);
        String refreshToken = tokenGenerator.generateRefreshToken(user);

        refreshTokenRepository.save(user.getId(), refreshToken);
        cookieManager.setAuthCookie(response, accessToken, refreshToken);

        return AuthResponse.LOGIN_SUCCESS;
    }

    public Response refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshTokenOpt = cookieManager.extractRefreshTokenFromCookies(request);

        if (refreshTokenOpt.isEmpty()) {
            return AuthResponse.TOKEN_INVALID;
        }

        String refreshToken = refreshTokenOpt.get();
        tokenValidator.validateToken(refreshToken);

        User user = userRepository.findByUsername(tokenExtractor.extractUsername(refreshToken))
                .orElseThrow(() -> new UnauthorizedException(AuthResponse.TOKEN_INVALID));
        try {
            String accessToken = tokenGenerator.generateAccessToken(user);
            String newRefreshToken = tokenGenerator.generateRefreshToken(user);

            refreshTokenRepository.deleteByUserIdAndToken(user.getId(), refreshToken);
            refreshTokenRepository.save(user.getId(), newRefreshToken);

            cookieManager.setAuthCookie(response, accessToken, newRefreshToken);

        } catch (DataAccessException e) {
            log.error("Redis 데이터 접근 중 예외 발생 {}", e.getMessage());
            throw new InternalServerErrorException(AuthResponse.REDIS_SERVER_ERROR);
        }

        return AuthResponse.TOKEN_REFRESH_SUCCESS;
    }

    public Response logout(HttpServletResponse response) {
        cookieManager.clearAuthCookie(response);
        return AuthResponse.LOGOUT_SUCCESS;
    }
}
