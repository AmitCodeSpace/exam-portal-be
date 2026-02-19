package com.example.amit.models;

import com.example.amit.common.constants.Role;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;


@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity implements Serializable {

    private String firstName;
    private String lastName;

//    @Column(nullable = false, unique = true)
//    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    private String phone;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    @Nullable
    private Instant lockedUntil;

    private boolean mfaEnabled;

    private String secret;

    @Builder.Default
    @Column(name = "single_session_only", nullable = false)
    private boolean singleSessionOnly = true;

    @Enumerated(EnumType.STRING)
    private Role role;


    public boolean isSystemAdmin() {
        return hasRole("SYSTEM_ADMIN");
    }

    public boolean isRegularUser() {
        return hasRole("USER");
    }


    public boolean hasRole(String roleName) {
        return role.name().equalsIgnoreCase(roleName);
    }
}


