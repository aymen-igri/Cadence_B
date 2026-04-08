package com.education.education.groups.DTO.response;

import com.education.education.groups.enums.GroupMemberStatus;
import com.education.education.groups.enums.GroupRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupMemberResponse(
        UUID membershipId,
        UUID userId,
        String firstName,
        String lastName,
        String username,
        GroupRole role,
        GroupMemberStatus status,
        LocalDateTime joinedAt
) {
}
