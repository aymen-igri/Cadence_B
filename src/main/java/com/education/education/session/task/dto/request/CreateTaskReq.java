package com.education.education.session.task.dto.request;

import com.education.education.session.task.enums.ETask;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTaskReq(
        @NotBlank(message = "Title is required")
        String title,

        @NotNull(message = "Session ID is required")
        UUID weeklySessionPlanId,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Task status is required")
        ETask status
) {
}
