package com.education.education.session.task.dto.request;

import com.education.education.session.task.enums.ETask;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskReq (
        @NotNull(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Status is required")
        ETask status
){
}
