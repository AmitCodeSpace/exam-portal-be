package com.example.amit.modules.authentication.dto.request;

import lombok.Builder;



@Builder
public record VerificationRequest(
         String email,
         String code
) {
}
