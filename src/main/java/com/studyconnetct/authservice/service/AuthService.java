package com.studyconnetct.authservice.service;

import com.studyconnetct.authservice.dto.AuthRequestDto;
import com.studyconnetct.authservice.dto.AuthResponseDto;
import com.studyconnetct.authservice.dto.RefreshTokenRequestDto;
import com.studyconnetct.authservice.dto.RegisterRequestDto;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto request);
    AuthResponseDto login(AuthRequestDto request);
    AuthResponseDto refreshToken(RefreshTokenRequestDto request);
    void logout(String userId);
}
