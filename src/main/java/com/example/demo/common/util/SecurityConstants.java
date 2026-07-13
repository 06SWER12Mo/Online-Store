package com.example.demo.common.util;

public final class SecurityConstants {

    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }

    // JWT Constants - These should be configured in application.properties
    // The actual values will be injected via @Value in configuration classes
    public static final String JWT_ISSUER = "online-store-api";
    public static final long JWT_EXPIRATION = 86400000; // 24 hours
    public static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7 days

    // Security Headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";

    // Public Endpoints (No authentication required)
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**",
            "/api/users/register",
            "/api/health/**",
            "/actuator/health",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/api/public/**"
    };

    // Admin Only Endpoints
    public static final String[] ADMIN_ENDPOINTS = {
            "/api/admin/**",
            "/api/users/**/enable",
            "/api/users/**/disable",
            "/api/users/**/lock",
            "/api/users/**/unlock",
            "/api/users/**/roles"
    };

    // User Endpoints (Authenticated users)
    public static final String[] USER_ENDPOINTS = {
            "/api/users/me",
            "/api/users/**/addresses",
            "/api/cart/**",
            "/api/orders/**",
            "/api/reviews/**"
    };

    // Role Names
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MANAGER = "ROLE_MANAGER";
    public static final String ROLE_USER = "ROLE_USER";

    // JWT Claims
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ROLES = "roles";

    // Error Messages
    public static final String ERROR_UNAUTHORIZED = "Authentication required. Please login.";
    public static final String ERROR_FORBIDDEN = "You do not have permission to access this resource.";
    public static final String ERROR_INVALID_TOKEN = "Invalid or expired token.";
    public static final String ERROR_MISSING_TOKEN = "Authorization token is missing.";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid username or password.";
    public static final String ERROR_ACCOUNT_LOCKED = "Your account has been locked. Please contact support.";
    public static final String ERROR_ACCOUNT_DISABLED = "Your account has been disabled. Please contact support.";
}