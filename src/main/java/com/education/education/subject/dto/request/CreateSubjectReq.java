package com.education.education.subject.dto.request;

import com.education.education.subject.enums.EPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSubjectReq(

        @NotBlank(message = "Subject name is required")
        String name,

        @NotNull(message = "Subject priority is required")
        EPriority priority,

        String description
) {
}
