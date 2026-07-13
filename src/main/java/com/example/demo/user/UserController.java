package com.example.demo.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.security.UserPrincipal;
import com.example.demo.user.dtos.UserResponse;
import com.example.demo.user.dtos.UserUpdateRequest;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Endpoints for managing users and profiles")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ========== USER ENDPOINTS (Any authenticated user) ==========

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserResponse response = userService.getUserById(userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete current user account", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // ========== ADMIN OR SELF ENDPOINTS ==========

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userSecurity.isCurrentUser(#id)")
    @Operation(summary = "Get user by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userSecurity.isCurrentUser(#id)")
    @Operation(summary = "Update user by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResponse> updateUserById(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userSecurity.isCurrentUser(#id)")
    @Operation(summary = "Delete user by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ========== ADMIN ONLY ENDPOINTS ==========

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all users (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Enable user account (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Disable user account (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/lock")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Lock user account (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unlock")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Unlock user account (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Verify user email (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> verifyUserEmail(@PathVariable Long id) {
        userService.verifyUserEmail(id);
        return ResponseEntity.ok().build();
    }

    // ========== ROLE MANAGEMENT (Admin only) ==========

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update user role (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        userService.updateUserRole(id, role);
        return ResponseEntity.ok().build();
    }

    // ========== STATISTICS (Admin only) ==========

    @GetMapping("/stats/count")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Long> countTotalUsers() {
        return ResponseEntity.ok(userService.countTotalUsers());
    }

    @GetMapping("/stats/active")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Long> countActiveUsers() {
        return ResponseEntity.ok(userService.countActiveUsers());
    }

    @GetMapping("/stats/registered")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Long> countUsersRegisteredBetween(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(userService.countUsersRegisteredBetween(startDate, endDate));
    }
}