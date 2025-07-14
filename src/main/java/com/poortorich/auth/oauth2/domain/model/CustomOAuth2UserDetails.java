package com.poortorich.auth.oauth2.domain.model;

import com.poortorich.user.entity.User;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class CustomOAuth2UserDetails implements OAuth2User {

    private final User user;

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public User getUser() {
        return user;
    }
}
