package com.example.amit.models;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;


@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sessions")
public class Session extends BaseEntity implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String sessionToken;

    private String userAgent;

    private String ipAddress;

    private String deviceFingerprint;

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    private Instant lastAccessedAt;

    private Instant expiresAt;
}
