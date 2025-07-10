package com.poortorich.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.global.response.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class TestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isTestUser = auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_TEST"));

        String message;
        if (isTestUser) {
            message = "테스트용 계정에서는 사용하실 수 없습니다.";
        } else {
            message = "접근 권한이 없습니다.";
        }

        BaseResponse jsonResponse = BaseResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(message)
                .build();
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(jsonResponse));
        writer.flush();
    }
}
