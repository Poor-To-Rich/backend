package com.poortorich.auth.controller;

import com.poortorich.auth.request.LoginRequest;
import com.poortorich.auth.service.AuthService;
import com.poortorich.global.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(
            @RequestBody @Valid LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        return BaseResponse.toResponseEntity(authService.login(loginRequest, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return BaseResponse.toResponseEntity(authService.refreshToken(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(
            HttpServletResponse response
    ) {
        return BaseResponse.toResponseEntity(authService.logout(response));
    }
}
