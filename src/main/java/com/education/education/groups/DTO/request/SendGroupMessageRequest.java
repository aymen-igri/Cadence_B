package com.education.education.groups.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record SendGroupMessageRequest(
        @NotBlank(message = "Message content cannot be empty")
        String content
) {
}
