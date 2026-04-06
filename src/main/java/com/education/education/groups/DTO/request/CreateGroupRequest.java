package com.education.education.groups.DTO.request;

import com.education.education.groups.enums.GroupPrivacy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateGroupRequest(
        @NotBlank(message = "Group name is required")
        String name,
        
        String description,
        
        @NotNull(message = "Privacy level is required")
        GroupPrivacy privacyLevel
) {
}
