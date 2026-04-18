package com.studyconnetct.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationRequestDto {
    
    @JsonProperty("access_token")
    @NotBlank(message = "Access token is required")
    private String accessToken;
}
