package com.example.amit.modules.authentication.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record SessionResponse(
        String deviceInfo,
        String ipAddress,
        Boolean isActive,
        LocalDateTime lastAccessedAt,
        LocalDateTime lastLoginAt,
        LocalDateTime expiresAt
) {
}
