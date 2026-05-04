package com.education.education.goal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateGoalReq(
        @NotBlank(message = "Title is required")
        String title,

        @NotNull(message = "Target hours per week is required")
        float targetHoursPerWeek,

        @NotNull(message = "Progress is required")
        float progress
) {
}
