package com.example.amit.models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification_recipients", uniqueConstraints = @UniqueConstraint(columnNames = {"notification_id", "employee_id"}))
public class NotificationRecipient extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User user;

    @Builder.Default
    @Column(nullable = false)
    private boolean seen = false;
}

