package com.education.education.goal.task.dto.request;

import com.education.education.goal.task.enums.ETask;

public record CreateTaskReq(
        String title,
        String description,
        ETask status
) {
}
