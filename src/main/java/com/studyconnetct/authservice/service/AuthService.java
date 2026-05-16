package com.studyconnetct.authservice.service;

import com.studyconnetct.authservice.dto.AuthRequestDto;
import com.studyconnetct.authservice.dto.AuthResponseDto;
import com.studyconnetct.authservice.dto.RegisterRequestDto;
import com.studyconnetct.authservice.dto.TokenValidationRequestDto;
import com.studyconnetct.authservice.dto.TokenValidationResponseDto;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto request);
    AuthResponseDto login(AuthRequestDto request);
    AuthResponseDto refreshToken(String refreshToken, String ipAddress);
    void logout(String userId);
    TokenValidationResponseDto validateToken(TokenValidationRequestDto request);
}
