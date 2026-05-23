package com.education.education.groups.DTO.request;

import com.education.education.groups.enums.GroupPrivacy;

public record GroupSearchRequest(
    String name,
    GroupPrivacy privacyLevel) {
}
