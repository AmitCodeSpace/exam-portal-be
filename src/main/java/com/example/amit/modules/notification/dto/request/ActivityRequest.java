package com.example.amit.modules.notification.dto.request;


import com.example.amit.common.constants.NotificationType;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;


@Builder
public record ActivityRequest(
        String title,
        String message,
        UUID actor,
        Set<UUID> targetEmployeeIds,
        NotificationType notificationType

) { }
