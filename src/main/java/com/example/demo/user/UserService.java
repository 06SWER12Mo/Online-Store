package com.example.demo.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    // Address methods
    AddressResponse addAddress(Long userId, AddressRequest request);

    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);

    void deleteAddress(Long userId, Long addressId);

    List<AddressResponse> getUserAddresses(Long userId);

    AddressResponse getDefaultAddress(Long userId);

    // Role methods
    void assignRoleToUser(Long userId, RoleName roleName);

    void removeRoleFromUser(Long userId, RoleName roleName);

    // Statistics
    long countTotalUsers();

    long countActiveUsers();

    long countUsersRegisteredBetween(String startDate, String endDate);
}