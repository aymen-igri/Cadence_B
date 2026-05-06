package com.education.education.session.task.dto.response;

import com.education.education.session.task.enums.ETask;

import java.util.UUID;

public record CreateTaskRes(
        UUID id,
        UUID weeklySessionPlanId,
        String title,
        String description,
        ETask status
) {
}
