package com.poortorich.admin.controller;

import com.poortorich.admin.facade.AdminFacade;
import com.poortorich.admin.request.UserRoleUpdateRequest;
import com.poortorich.admin.response.enums.AdminResponse;
import com.poortorich.global.response.BaseResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminFacade adminFacade;

    @PutMapping("/{userId}/role")
    public ResponseEntity<BaseResponse> updateUserRole(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable @Nullable Long userId,
            @RequestBody UserRoleUpdateRequest userRoleUpdateRequest
    ) {
        adminFacade.updateUserRole(userId, userRoleUpdateRequest);
        return BaseResponse.toResponseEntity(AdminResponse.ROLE_UPDATE_SUCCESS);
    }
}
