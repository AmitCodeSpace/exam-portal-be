package com.example.amit.modules.authentication.dto.response;


import lombok.Builder;

@Builder
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AuthenticationResponse(
         String accessToken,
         String refreshToken,
         boolean mfaEnabled,
         String secretImageUri
) {
}
