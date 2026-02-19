package com.example.amit.modules.authentication.dto.request;

import com.example.amit.common.constants.Role;
import com.example.amit.models.User;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record RegisterRequest (
        @NotNull(message = "Firstname can not be empty !!")
        String firstName,

        @NotNull(message = "Lastname can not be empty !!")
        String lastName,

        @NotNull(message = "Email can not be empty !!")
        String email,

        @NotNull(message = "Password can not be empty !!")
        String password,

        boolean mfaEnabled

){

  public User buildUpdateEmployee(User user) {
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setEmail(email);
      user.setPassword(password);
      user.setRole(Role.SYSTEM_ADMIN);
      user.setMfaEnabled(mfaEnabled);
      return user;
  }
}
