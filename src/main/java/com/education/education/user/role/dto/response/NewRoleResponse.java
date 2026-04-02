package com.education.education.user.role.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NewRoleResponse(
        UUID id,
        String role,
        LocalDateTime createdAt
) {
}
