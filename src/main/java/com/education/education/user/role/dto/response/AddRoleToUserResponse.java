package com.education.education.user.role.dto.response;


import java.util.UUID;

public record AddRoleToUserResponse(
        UUID id,
        String firstName,
        String lastName,
        String role
) {
}
