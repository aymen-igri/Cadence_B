package com.education.education.goal.task.dto.response;

import com.education.education.goal.task.enums.ETask;

import java.util.UUID;

public record CreateTaskRes(
        UUID id,
        String title,
        String description,
        ETask status
) {
}
