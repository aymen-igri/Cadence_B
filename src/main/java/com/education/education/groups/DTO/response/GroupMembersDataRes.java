package com.education.education.groups.DTO.response;

import java.time.LocalDate;
import java.util.UUID;

import com.education.education.groups.enums.GroupRole;

public record GroupMembersDataRes(
    UUID id,
    String firstName,
    String lastName,
    GroupRole role,
    LocalDate joinedAt) {
}
