package com.example.demo.auth;

import com.example.demo.user.RefreshToken;
import com.example.demo.user.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken validateRefreshToken(String token);

    void revokeRefreshToken(String token);

    void revokeAllUserTokens(Long userId);

    RefreshToken refreshAccessToken(String refreshToken);

    void deleteExpiredTokens();
}