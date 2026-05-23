package com.education.education.groups.DTO.response;

import java.util.UUID;

import com.education.education.groups.enums.GroupPrivacy;

public record GroupSearchResponse(
    UUID id,
    String name,
    GroupPrivacy privacyLevel) {
}
