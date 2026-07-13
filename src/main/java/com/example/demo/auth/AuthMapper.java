package com.example.demo.auth;

import com.example.demo.auth.dtos.AuthResponse;
import com.example.demo.auth.dtos.RegisterRequest;
import com.example.demo.auth.dtos.UserProfileResponse;
import com.example.demo.user.Role;
import com.example.demo.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuthMapper {

    private final PasswordEncoder passwordEncoder;

    public AuthMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEnabled(true);
        user.setLocked(false);
        user.setEmailVerified(false);
        
        // ✅ Default role is USER (simple enum, no entity!)
        user.setRole(Role.USER);
        
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public UserProfileResponse toUserProfileResponse(User user) {
        return new UserProfileResponse(user);
    }

    public AuthResponse toAuthResponse(String accessToken, String refreshToken, Long expiresIn, User user) {
        UserProfileResponse userProfile = toUserProfileResponse(user);
        return new AuthResponse(accessToken, refreshToken, expiresIn, userProfile);
    }
}