package com.education.education.user.role.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddRoleToUserRequest (
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Role name is required")
        String roleName
){
}
