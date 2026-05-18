package com.studyconnetct.authservice.controller;

import com.studyconnetct.authservice.dto.*;
import com.studyconnetct.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        log.info("Register endpoint called for email: {}", request.getEmail());
        AuthResponseDto response = authService.register(request);
        ResponseCookie cookie = createRefreshTokenCookie(response.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        log.info("Login endpoint called for email: {}", request.getEmail());
        AuthResponseDto response = authService.login(request);
        ResponseCookie cookie = createRefreshTokenCookie(response.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@CookieValue(value = "refresh_token", required = false) String refreshToken,
                                                    HttpServletRequest request) {
        log.info("Refresh token endpoint called");
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RuntimeException("Refresh token cookie is missing");
        }
        AuthResponseDto response = authService.refreshToken(refreshToken, request.getRemoteAddr());
        ResponseCookie cookie = createRefreshTokenCookie(response.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }
    
    @PostMapping("/logout/{userId}")
    public ResponseEntity<Void> logout(@PathVariable String userId) {
        log.info("Logout endpoint called for user: {}", userId);
        authService.logout(userId);
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
    
    @PostMapping("/validate-token")
    public ResponseEntity<TokenValidationResponseDto> validateToken(@Valid @RequestBody TokenValidationRequestDto request) {
        log.info("Validate token endpoint called");
        TokenValidationResponseDto response = authService.validateToken(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running");
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)   // HTTPS requerido para SameSite=None
                .path("/")
                .maxAge(86400)
                .sameSite("None")  // Permite envío cross-site (frontend y gateway en distintos subdominios)
                .build();
    }
}
