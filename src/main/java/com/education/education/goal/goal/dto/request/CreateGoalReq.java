package com.education.education.goal.goal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateGoalReq(
        @NotNull(message = "Subject ID is required")
        UUID subjectId,

        @NotBlank(message = "Title is required")
        String title,

        @NotNull(message = "Target hours per week is required")
        float targetHoursPerWeek,

        @NotNull(message = "Progress is required")
        float progress
) {
}
