package com.education.education.user.role.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NewRoleRequest (
        @NotBlank(message = "Role is required")
        String role
) {
}
