package com.education.education.user.user.dto.response;

import com.education.education.user.user.enums.EGender;
import com.education.education.user.user.enums.EStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AddUserResponse(
        UUID id,
        String firstName,
        String LastName,
        EGender gender,
        String email,
        String phone,
        String username,
        EStatus status,
        LocalDateTime createdAt
) {
}
