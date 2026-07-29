package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.enums.Role;

public interface UserService {

    UserResponse registerUser(UserRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    UserResponse getUserByUsername(String username);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    Page<UserResponse> getAllUsers(Pageable pageable);

    void enableUser(Long id);

    void disableUser(Long id);

    void lockUser(Long id);

    void unlockUser(Long id);

    void verifyUserEmail(Long id);

    void requestEmailVerification(Long id);

    void cancelEmailVerification(Long id);

    // ✅ Simple role update - just the ENUM
    void updateUserRole(Long userId, Role role);

    // Statistics
    long countTotalUsers();

    long countActiveUsers();

    long countUsersRegisteredBetween(String startDate, String endDate);
}