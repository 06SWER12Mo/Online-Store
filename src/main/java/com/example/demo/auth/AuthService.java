package com.example.demo.auth;

import com.example.demo.auth.dtos.AuthResponse;
import com.example.demo.auth.dtos.LoginRequest;
import com.example.demo.auth.dtos.RegisterRequest;
import com.example.demo.auth.dtos.UserProfileResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    AuthResponse refreshToken(String refreshToken);

    void logout(String refreshToken);

    UserProfileResponse getCurrentUserProfile(Long userId);
}