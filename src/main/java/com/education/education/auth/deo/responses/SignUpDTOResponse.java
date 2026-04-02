package com.education.education.auth.deo.responses;

import com.education.education.user.user.enums.EGender;

import java.time.LocalDateTime;
import java.util.UUID;

public record SignUpDTOResponse(
        UUID id,
        String firstName,
        String lastName,
        EGender gender,
        String email,
        String phone,
        String username,
        LocalDateTime createdAt
) {
}
