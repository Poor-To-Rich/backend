package com.poortorich.auth.jwt.repository.interfaces;

public interface RefreshTokenRepository {

    void saveRefreshToken(Long userId, String token);

    boolean existsByUserIdAndToken(Long userId, String token);

    void deleteByUserIdAndToken(Long userId, String token);

    void deleteAllByUserId(Long userId);
}
