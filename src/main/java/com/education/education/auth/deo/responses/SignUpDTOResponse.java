package com.education.education.auth.deo.responses;

import java.util.UUID;

public record SignUpDTOResponse(
        Tokens tokens,
        AuthUser user
) {
    public record Tokens(
            String accessToken,
            String refreshToken
    ) {}

    public record AuthUser(
            UUID id,
            String firstName,
            String lastName,
            String username,
            String email,
            String phone,
            String gender,
            String role
    ) {}
}

