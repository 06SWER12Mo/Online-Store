package com.example.demo.auth;

import com.example.demo.auth.dtos.AuthResponse;
import com.example.demo.auth.dtos.LoginRequest;
import com.example.demo.auth.dtos.RegisterRequest;
import com.example.demo.auth.dtos.UserProfileResponse;
import com.example.demo.security.JwtService;
import com.example.demo.security.UserPrincipal;
import com.example.demo.user.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RefreshTokenService refreshTokenService,
                           JwtService jwtService,
                           AuthMapper authMapper,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.authMapper = authMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            User user = userRepository.findByUsername(request.getUsernameOrEmail())
                    .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                    .orElse(null);

            if (user == null) {
                throw new BadCredentialsException("Invalid username or password");
            }

            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            if (!passwordMatches) {
                throw new BadCredentialsException("Invalid username or password");
            }

            if (!user.isEnabled()) {
                throw new IllegalStateException("User account is disabled");
            }

            if (user.isLocked()) {
                throw new IllegalStateException("User account is locked");
            }

            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            String accessToken = jwtService.generateToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return authMapper.toAuthResponse(
                    accessToken,
                    refreshToken.getToken(),
                    jwtService.getTokenExpirationMs(),
                    user
            );
        } catch (BadCredentialsException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        }

        try {
            User user = authMapper.toUser(request);
            
            // ✅ Default role is USER (simple enum, no entity!)
            user.setRole(Role.USER);

            User savedUser = userRepository.save(user);
            System.out.println("✅ User registered: " + savedUser.getUsername() + " (ID: " + savedUser.getId() + ", Role: " + savedUser.getRole() + ")");

            // Auto-login after registration
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtService.generateToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

            return authMapper.toAuthResponse(
                    accessToken,
                    refreshToken.getToken(),
                    jwtService.getTokenExpirationMs(),
                    savedUser
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        try {
            RefreshToken validatedToken = refreshTokenService.validateRefreshToken(refreshToken);
            User user = validatedToken.getUser();

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
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token: " + e.getMessage());
        }
    }

    @Override
    public void logout(String refreshToken) {
        try {
            refreshTokenService.revokeRefreshToken(refreshToken);
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            System.err.println("Logout warning: " + e.getMessage());
        }
    }

    @Override
    public UserProfileResponse getCurrentUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return authMapper.toUserProfileResponse(user);
    }
}