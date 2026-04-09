package com.education.education.groups.DTO.request;

import com.education.education.groups.enums.GroupPrivacy;

public record UpdateGroupRequest(
        String name,
        String description,
        GroupPrivacy privacyLevel
) {
}