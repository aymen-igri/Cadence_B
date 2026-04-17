package com.education.education.groups.DTO.response;

import com.education.education.groups.enums.JoinRequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record JoinRequestResponse(
        UUID id,
        UUID groupId,
        UUID userId,
        String firstName,
        String lastName,
        String username,
        JoinRequestStatus status,
        LocalDateTime requestedAt
) {
}