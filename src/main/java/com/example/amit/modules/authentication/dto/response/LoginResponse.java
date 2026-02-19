package com.example.amit.modules.authentication.dto.response;

import lombok.Builder;


@Builder
public record LoginResponse(
        String username,
        String accessToken,
        Long expiresIn
) {
}

