package com.poortorich.security.filter.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.auth.jwt.fixture.JwtUserFixture;
import com.poortorich.auth.jwt.util.JwtTokenExtractor;
import com.poortorich.auth.jwt.util.JwtTokenManager;
import com.poortorich.auth.jwt.validator.JwtTokenValidator;
import com.poortorich.auth.response.enums.AuthResponse;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.Response;
import com.poortorich.security.constants.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenValidator tokenValidator;

    @Mock
    private JwtTokenExtractor tokenExtractor;

    @Mock
    private JwtTokenManager cookieManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrintWriter writer;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String accessToken;
    private UserDetails userDetails;
    private SecurityContext securityContext;
    private ObjectMapper testObjectMapper;

    @BeforeEach
    void setUp() throws IOException {
        accessToken = "access-token";
        testObjectMapper = new ObjectMapper();

        userDetails = mock(UserDetails.class);
        securityContext = mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);
        lenient().when(userDetails.getUsername()).thenReturn(JwtUserFixture.TEST_USERNAME);
        lenient().when(response.getWriter()).thenReturn(writer);
    }

    @AfterEach
    void teatDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("허용된 엔드포인트 요청 시 인증 필터를 건너뛰어야 함")
    void doFilterInternal_ShouldSkipAuthentication_WhenRequestingPermitAllEndpoint()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn(SecurityConstants.PERMIT_ALL_ENDPOINTS[0]);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenExtractor, tokenExtractor, tokenValidator);
    }

    @Test
    @DisplayName("유효한 액세스 토큰이 있을 때 인증을 성공해야 함")
    void doFilterInternal_ShouldAuthenticateUser_WhenAccessTokenIsValid() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/protected");
        when(cookieManager.extractAccessTokenFromHeader(request)).thenReturn(Optional.of(accessToken));
        when(tokenValidator.isTokenExpired(accessToken)).thenReturn(false);
        when(tokenExtractor.extractUsername(accessToken)).thenReturn(JwtUserFixture.TEST_USERNAME);
        when(userDetailsService.loadUserByUsername(JwtUserFixture.TEST_USERNAME)).thenReturn(userDetails);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(securityContext).setAuthentication(authenticationCaptor.capture());

        Authentication authentication = authenticationCaptor.getValue();
        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);

        verify(filterChain).doFilter(request, response);

        verify(response, never()).setStatus(anyInt());
        verify(response, never()).getWriter();
        verify(cookieManager, never()).clearAuthTokens(response);
    }

    @Test
    @DisplayName("액세스 토큰 쿠기가 없을 때 토큰 무효 응답을 반환해야 함")
    void doFilterInternal_ShouldReturnTokenInvalidResponse_WhenAccessTokenIsMissing()
            throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/api/protected");
        when(cookieManager.extractAccessTokenFromHeader(request)).thenReturn(Optional.empty());

        String expectedJsonResponse = getExpectedJsonResponse(AuthResponse.TOKEN_INVALID);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verifyErrorResponse(expectedJsonResponse);
        verifyNoInteractions(filterChain);
        verifyNoInteractions(tokenValidator, tokenExtractor, tokenExtractor);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    @DisplayName("액세스 토큰이 만료되었을 때 토큰 만료 응답을 반환해야 함")
    void doFilterInternal_ShouldReturnTokenExpiredResponse_WhenAccessTokenIsExpired()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/protected");
        when(cookieManager.extractAccessTokenFromHeader(request)).thenReturn(Optional.of(accessToken));
        when(tokenValidator.isTokenExpired(accessToken)).thenReturn(true);

        String expectedJsonResponse = getExpectedJsonResponse(AuthResponse.ACCESS_TOKEN_EXPIRED);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verifyErrorResponse(expectedJsonResponse);
        verifyNoInteractions(filterChain);
        verifyNoInteractions(tokenExtractor, userDetails);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    @DisplayName("액세스 토큰에서 사용자 이름을 추출할 수 없을 때 토큰 무효 응답을 반환해야 함")
    void doFilterInternal_ShouldReturnTokenInvalidResponse_WhenUsernameExtractionFails()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/protected");
        when(cookieManager.extractAccessTokenFromHeader(request)).thenReturn(Optional.of(accessToken));
        when(tokenValidator.isTokenExpired(accessToken)).thenReturn(false);
        when(tokenExtractor.extractUsername(accessToken)).thenReturn(null);

        String expectedJsonResponse = getExpectedJsonResponse(AuthResponse.TOKEN_INVALID);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verifyErrorResponse(expectedJsonResponse);
    }

    private String getExpectedJsonResponse(Response expectedResponse) throws JsonProcessingException {
        return testObjectMapper.writeValueAsString(
                BaseResponse.builder()
                        .status(expectedResponse.getHttpStatus().value())
                        .message(expectedResponse.getMessage())
                        .build()
        );
    }

    private String getActualJsonResponse() {
        ArgumentCaptor<char[]> bufferCaptor = ArgumentCaptor.forClass(char[].class);
        ArgumentCaptor<Integer> offCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> lenCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(writer).write(bufferCaptor.capture(), offCaptor.capture(), lenCaptor.capture());
        return new String(bufferCaptor.getValue(), offCaptor.getValue(), lenCaptor.getValue());
    }

    private void verifyErrorResponse(String expectedJsonResponse) {
        assertThat(getActualJsonResponse()).isEqualTo(expectedJsonResponse);

        verify(cookieManager).clearAuthTokens(response);
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setCharacterEncoding("UTF-8");
    }
}
