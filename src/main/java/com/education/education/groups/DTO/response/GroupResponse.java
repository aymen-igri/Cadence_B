package com.education.education.groups.DTO.response;

import com.education.education.groups.enums.GroupPrivacy;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupResponse(
        UUID id,
        String name,
        String description,
        GroupPrivacy privacyLevel,
        int membersCount,
        LocalDateTime createdAt,
        String membershipId
) {
}
