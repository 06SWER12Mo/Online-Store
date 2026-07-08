package com.example.demo.auth;

import com.example.demo.security.JwtService;
import com.example.demo.security.UserPrincipal;
import com.example.demo.user.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           RefreshTokenService refreshTokenService,
                           JwtService jwtService,
                           AuthMapper authMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.authMapper = authMapper;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        User user = getUserByUsernameOrEmail(request.getUsernameOrEmail());

        // Check if user is enabled
        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        if (user.isLocked()) {
            throw new RuntimeException("User account is locked");
        }

        // Update last login
        userRepository.updateLastLogin(user.getId(), java.time.LocalDateTime.now());

        // Generate tokens
        String accessToken = jwtService.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        // Return response
        return authMapper.toAuthResponse(
                accessToken,
                refreshToken.getToken(),
                jwtService.getTokenExpirationMs(),
                user
        );
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if email or username already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken: " + request.getUsername());
        }

        // Create new user
        User user = authMapper.toUser(request);

        // Assign default ROLE_USER
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));
        user.addRole(userRole);

        User savedUser = userRepository.save(user);

        // Auto-login after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate tokens
        String accessToken = jwtService.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return authMapper.toAuthResponse(
                accessToken,
                refreshToken.getToken(),
                jwtService.getTokenExpirationMs(),
                savedUser
        );
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken validatedToken = refreshTokenService.validateRefreshToken(refreshToken);
        User user = validatedToken.getUser();

        // Generate new access token
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );
        String newAccessToken = jwtService.generateToken(authentication);

        return authMapper.toAuthResponse(
                newAccessToken,
                refreshToken,
                jwtService.getTokenExpirationMs(),
                user
        );
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
        SecurityContextHolder.clearContext();
    }

    @Override
    public UserProfileResponse getCurrentUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return authMapper.toUserProfileResponse(user);
    }

    // Helper methods
    private User getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByEmailOrUsername(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new RuntimeException("User not found with username or email: " + usernameOrEmail));
    }
}