package com.poortorich.auth.oauth2.handler;

import com.poortorich.auth.jwt.util.JwtTokenGenerator;
import com.poortorich.auth.jwt.util.JwtTokenManager;
import com.poortorich.auth.oauth2.domain.model.CustomOAuth2UserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenGenerator tokenGenerator;
    private final JwtTokenManager tokenManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2UserDetails userDetails = (CustomOAuth2UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
        String accessToken = tokenGenerator.generateAccessToken(userDetails.getUser());
        String refreshToken = tokenGenerator.generateRefreshToken(userDetails.getUser());
        tokenManager.setRefreshTokens(response, refreshToken);

        response.sendRedirect("https://localhost:5173/");
    }
}
