package com.education.education.goal.dto.request;


import com.education.education.goal.task.dto.request.CreateTaskReq;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateGoalRequest(
        @NotNull(message = "Goal details are required")
        CreateGoalReq goal,

        @NotNull(message = "Tasks are required")
        List<CreateTaskReq> tasks
) {
}
