package com.studyconnetct.authservice.controller;

import com.studyconnetct.authservice.dto.AuthRequestDto;
import com.studyconnetct.authservice.dto.AuthResponseDto;
import com.studyconnetct.authservice.dto.RefreshTokenRequestDto;
import com.studyconnetct.authservice.dto.RegisterRequestDto;
import com.studyconnetct.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        log.info("Register endpoint called for email: {}", request.getEmail());
        AuthResponseDto response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        log.info("Login endpoint called for email: {}", request.getEmail());
        AuthResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        log.info("Refresh token endpoint called");
        AuthResponseDto response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout/{userId}")
    public ResponseEntity<Void> logout(@PathVariable String userId) {
        log.info("Logout endpoint called for user: {}", userId);
        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running");
    }
}
