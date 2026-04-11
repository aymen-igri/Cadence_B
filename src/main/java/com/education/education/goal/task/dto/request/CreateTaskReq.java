package com.education.education.goal.task.dto.request;

import com.education.education.goal.task.enums.ETask;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTaskReq(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Task status is required")
        ETask status
) {
}
