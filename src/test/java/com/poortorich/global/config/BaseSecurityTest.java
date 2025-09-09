package com.poortorich.global.config;

import com.poortorich.auth.jwt.util.JwtTokenExtractor;
import com.poortorich.auth.jwt.util.JwtTokenManager;
import com.poortorich.auth.jwt.validator.JwtTokenValidator;
import com.poortorich.auth.oauth2.handler.CustomOAuth2LoginSuccessHandler;
import com.poortorich.auth.oauth2.service.CustomOAuth2UserService;
import com.poortorich.security.handler.TestAccessDeniedHandler;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(MockitoExtension.class)
public class BaseSecurityTest {

    @MockitoBean
    protected JwtTokenValidator tokenValidator;

    @MockitoBean
    protected JwtTokenExtractor tokenExtractor;

    @MockitoBean
    protected JwtTokenManager cookieManager;

    @MockitoBean
    protected UserDetailsService userDetailsService;

    @MockitoBean
    protected PasswordEncoder passwordEncoder;

    @MockitoBean
    protected CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    protected CustomOAuth2LoginSuccessHandler customOAuth2LoginSuccessHandler;

    @MockitoBean
    protected TestAccessDeniedHandler accessDeniedHandler;
}
