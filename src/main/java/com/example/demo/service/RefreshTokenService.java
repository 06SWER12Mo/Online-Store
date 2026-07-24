package com.example.demo.service;

import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken validateRefreshToken(String token);

    void revokeRefreshToken(String token);

    void revokeAllUserTokens(Long userId);

    RefreshToken refreshAccessToken(String refreshToken);

    void deleteExpiredTokens();
}