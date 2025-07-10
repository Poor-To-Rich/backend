package com.poortorich.admin.facade;

import com.poortorich.admin.request.UserRoleUpdateRequest;
import com.poortorich.admin.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminFacade {

    private final UserManagementService userManagementService;

    public void updateUserRole(Long userId, UserRoleUpdateRequest userRoleUpdateRequest) {
        userManagementService.updateUserRole(userId, userRoleUpdateRequest);
    }
}
