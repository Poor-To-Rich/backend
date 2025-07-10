package com.poortorich.admin.service;

import com.poortorich.admin.request.UserRoleUpdateRequest;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import com.poortorich.user.entity.enums.Role;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.response.enums.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;

    @Transactional
    public void updateUserRole(Long userId, UserRoleUpdateRequest userRoleUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND));
        Role role = Role.from(userRoleUpdateRequest.getRole());
        user.updateRole(role);
    }
}
