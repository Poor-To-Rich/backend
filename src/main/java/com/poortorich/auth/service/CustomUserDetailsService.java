package com.poortorich.auth.service;

import com.poortorich.auth.response.enums.AuthResponse;
import com.poortorich.global.exceptions.UnauthorizedException;
import com.poortorich.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException(AuthResponse.TOKEN_INVALID));
    }
}
