package com.poortorich.global.config;

import com.poortorich.auth.jwt.util.JwtCookieManager;
import com.poortorich.auth.jwt.util.JwtTokenExtractor;
import com.poortorich.auth.jwt.validator.JwtTokenValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(MockitoExtension.class)
public class BaseSecurityTest {

    @MockitoBean
    protected JwtTokenValidator tokenValidator;

    @MockitoBean
    protected JwtTokenExtractor tokenExtractor;

    @MockitoBean
    protected JwtCookieManager cookieManager;

    @MockitoBean
    protected UserDetailsService userDetailsService;
}
