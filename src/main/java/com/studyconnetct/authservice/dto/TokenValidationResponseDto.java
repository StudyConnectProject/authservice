package com.studyconnetct.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationResponseDto {
    
    @JsonProperty("is_valid")
    private Boolean isValid;
    
    @JsonProperty("is_expired")
    private Boolean isExpired;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("roles")
    private Set<String> roles;
    
    @JsonProperty("expires_at")
    private Long expiresAt;
}
