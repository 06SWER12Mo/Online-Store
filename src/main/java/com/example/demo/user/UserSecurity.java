package com.example.demo.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            return user.getId().equals(userId);
        }
        return false;
    }

    // ✅ Helper to check if current user has a specific role
    public boolean hasRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            return user.getRole() == role;
        }
        return false;
    }

    // ✅ Helper to check if current user is admin or higher
    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    // ✅ Helper to check if current user is manager or higher
    public boolean isManagerOrHigher() {
        return hasRole(Role.ADMIN) || hasRole(Role.MANAGER);
    }
}