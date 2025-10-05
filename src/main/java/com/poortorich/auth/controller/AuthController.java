package com.poortorich.auth.controller;

import com.poortorich.auth.request.LoginRequest;
import com.poortorich.auth.response.enums.AuthResponse;
import com.poortorich.auth.service.AuthService;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/health")
    public ResponseEntity<BaseResponse> healthCheck() {
        return BaseResponse.toResponseEntity(AuthResponse.HEALTH_CHECK);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(
            @RequestBody @Valid LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        return DataResponse.toResponseEntity(AuthResponse.LOGIN_SUCCESS, authService.login(loginRequest, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return DataResponse.toResponseEntity(
                AuthResponse.TOKEN_REFRESH_SUCCESS, authService.refreshToken(request, response)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(
            HttpServletResponse response,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return BaseResponse.toResponseEntity(authService.logout(response));
    }

    @PatchMapping("/kakao/revert")
    public ResponseEntity<BaseResponse> revertKakaoLogin(@AuthenticationPrincipal UserDetails userDetails) {
        authService.revertUser(userDetails.getUsername());
        return BaseResponse.toResponseEntity(AuthResponse.KAKAO_LOGIN_REVERT_SUCCESS);
    }
}
