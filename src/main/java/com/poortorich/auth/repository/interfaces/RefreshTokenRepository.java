package com.poortorich.auth.repository.interfaces;

public interface RefreshTokenRepository {

    void save(Long userId, String token);

    boolean existsByUserIdAndToken(Long userId, String token);

    void deleteByUserIdAndToken(Long userId, String token);

    void deleteAllByUserId(Long userId);
}
