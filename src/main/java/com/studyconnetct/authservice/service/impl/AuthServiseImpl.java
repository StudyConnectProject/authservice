package com.studyconnetct.authservice.service.impl;

import com.studyconnetct.authservice.dto.*;
import com.studyconnetct.authservice.entity.RefreshToken;
import com.studyconnetct.authservice.entity.Role;
import com.studyconnetct.authservice.entity.User;
import com.studyconnetct.authservice.entity.UserRole;
import com.studyconnetct.authservice.repository.RefreshTokenRepository;
import com.studyconnetct.authservice.repository.RoleRepository;
import com.studyconnetct.authservice.repository.UserRepository;
import com.studyconnetct.authservice.service.AuthService;
import com.studyconnetct.authservice.util.HashUtil;
import com.studyconnetct.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiseImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final HashUtil hashUtil;
    
    @Override
    public AuthResponseDto register(RegisterRequestDto request) {
        log.info("Registering new user: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Get role from database
        Role role = roleRepository.findByName(request.getRole().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role not found "));
        
        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(hashUtil.hashPassword(request.getPassword()))
                .isActive(true)
                .build();
        
        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getId());
        
        // Crear relación UserRole
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        user.getUserRoles().add(userRole);

        // Guardar nuevamente con relación
        user = userRepository.save(user);

        // Obtener roles
        Set<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toSet());

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        RefreshToken token = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hashUtil.hashToken(refreshToken))
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        refreshTokenRepository.save(token);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public AuthResponseDto login(AuthRequestDto request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsActive()) {
            throw new RuntimeException("User inactive");
        }

        if (!hashUtil.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        Set<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toSet());

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        RefreshToken token = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hashUtil.hashToken(refreshToken))
                .ipAddress(request.getIpAddress())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        refreshTokenRepository.save(token);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {

        String tokenHash = hashUtil.hashToken(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHashAndIsRevokedFalse(tokenHash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Expired token");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toSet());

        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles);
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

        refreshToken.setIsRevoked(true);
        refreshTokenRepository.save(refreshToken);

        RefreshToken newToken = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hashUtil.hashToken(newRefreshToken))
                .ipAddress(request.getIpAddress())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        refreshTokenRepository.save(newToken);

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(String userId) {
        refreshTokenRepository.deleteByUserId(java.util.UUID.fromString(userId));
    }

    @Override
    public TokenValidationResponseDto validateToken(TokenValidationRequestDto request) {
        log.info("Validating token");
        
        if (!jwtUtil.validateToken(request.getAccessToken())) {
            throw new RuntimeException("Invalid token");
        }
        
        if (jwtUtil.isTokenExpired(request.getAccessToken())) {
            throw new RuntimeException("Token expired");
        }
        
        java.util.UUID userId = jwtUtil.getUserIdFromToken(request.getAccessToken());
        String email = jwtUtil.getEmailFromToken(request.getAccessToken());
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Set<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toSet());
        
        return TokenValidationResponseDto.builder()
                .isValid(true)
                .isExpired(false)
                .message("Token is valid")
                .userId(userId.toString())
                .email(email)
                .roles(roles)
                .expiresAt(jwtUtil.getTokenExpirationTime())
                .build();
    }

    private AuthResponseDto buildAuthResponse(User user, String accessToken, String refreshToken) {

        Set<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toSet());

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(AuthResponseDto.UserDto.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .roles(roles)
                        .build())
                .build();
    }
}
