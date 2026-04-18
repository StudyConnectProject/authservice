package com.studyconnetct.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequestDto {
    
    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("ip_address")
    private String ipAddress;
}
