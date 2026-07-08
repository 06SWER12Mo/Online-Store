package com.example.demo.auth;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    AuthResponse refreshToken(String refreshToken);

    void logout(String refreshToken);

    UserProfileResponse getCurrentUserProfile(Long userId);
}