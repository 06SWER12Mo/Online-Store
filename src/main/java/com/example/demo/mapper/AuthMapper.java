package com.example.demo.mapper;

import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.dto.response.UserProfileResponse;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;

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