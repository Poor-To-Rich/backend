package com.poortorich.security.filter.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.auth.jwt.util.JwtTokenExtractor;
import com.poortorich.auth.jwt.util.JwtTokenManager;
import com.poortorich.auth.jwt.validator.JwtTokenValidator;
import com.poortorich.auth.response.enums.AuthResponse;
import com.poortorich.global.exceptions.UnauthorizedException;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.Response;
import com.poortorich.security.constants.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final JwtTokenValidator tokenValidator;
    private final JwtTokenExtractor tokenExtractor;
    private final JwtTokenManager jwtManager;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (isAuthenticated() || Arrays.asList(SecurityConstants.PERMIT_ALL_ENDPOINTS)
                .contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            if (!processAccessToken(request, response)) {
                processRefreshToken(request, response);
            }
        } catch (UnauthorizedException exception) {
            jwtManager.clearAuthTokens(response);
            setResponseMessage(response, exception.getResponse());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private void processRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshToken = jwtManager.extractRefreshTokenFromCookies(request);

        if (refreshToken.isEmpty()) {
            throw new UnauthorizedException(AuthResponse.TOKEN_INVALID);
        }

        tokenValidator.validateToken(refreshToken.get());
        UserDetails userDetails = loadUserDetailsFromToken(refreshToken.get());
        setAuthentication(userDetails, request);
    }

    private boolean processAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> accessToken = jwtManager.extractAccessTokenFromHeader(request);

        if (accessToken.isEmpty()) {
            throw new UnauthorizedException(AuthResponse.TOKEN_INVALID);
        }

        try {
            if (tokenValidator.isTokenExpired(accessToken.get())) {
                throw new UnauthorizedException(AuthResponse.ACCESS_TOKEN_EXPIRED);
            }
            UserDetails userDetails = loadUserDetailsFromToken(accessToken.get());
            setAuthentication(userDetails, request);
        } catch (UnauthorizedException exception) {
            if (exception.getResponse().equals(AuthResponse.ACCESS_TOKEN_EXPIRED)) {
                throw exception;
            }
            return false;
        }

        return true;
    }

    private UserDetails loadUserDetailsFromToken(String token) {
        String username = tokenExtractor.extractUsername(token);

        if (username == null) {
            throw new UnauthorizedException(AuthResponse.TOKEN_INVALID);
        }

        return userDetailsService.loadUserByUsername(username);
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private void setResponseMessage(HttpServletResponse response, Response responseInformation) throws IOException {
        response.setStatus(responseInformation.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        BaseResponse responseMessage = BaseResponse
                .builder()
                .status(responseInformation.getHttpStatus().value())
                .message(responseInformation.getMessage())
                .build();

        objectMapper.writeValue(response.getWriter(), responseMessage);
    }
}
