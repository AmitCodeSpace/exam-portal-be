package com.example.amit.modules.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record ForgotPasswordRequest(
        @NotBlank(message = "Username cannot be empty")
        @Size(min = 4, max = 50, message = "Username must be between 3 and 50 characters")
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String newPassword,

        @NotBlank(message = "New password cannot be empty")
        @Size(min = 8, message = "New password must be at least 8 characters")
        String confirmNewPassword
) {
    public boolean isValidRequest() {
        return newPassword != null && confirmNewPassword != null &&
                newPassword.trim().equals(confirmNewPassword.trim());
    }
}
