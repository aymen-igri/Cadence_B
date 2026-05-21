package com.education.education.groups.DTO.response;

import com.education.education.groups.enums.GroupPrivacy;

public record TopActiveGroupsResponse(
    String name,
    GroupPrivacy groupPrivacy,
    Integer memberscount) {
}
